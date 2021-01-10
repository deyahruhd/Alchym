package jard.alchym.client.helper;

import jard.alchym.AlchymReference;
import jard.alchym.api.book.impl.ContentPage;
import jard.alchym.helper.MathHelper;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.*;

/***
 *  BookHelper
 *  Contains various helper methods for book content processing and rendering.
 *
 *  Created by jard at 20:07 on January, 01, 2021.
 ***/
public class BookHelper {
    public static final LiteralText EMPTY_SPACE = (LiteralText) new LiteralText ("").append (" ");

    /**
     * Splits the input {@code text} into a word-wrapped array of text components, suitable for rendering.
     * Minimizes the sum of the squared widths of whitespace at the end of each line, returning a block
     * of text with minimal raggedness.
     *
     * @param text       input text to split
     * @param renderer   the {@link TextRenderer} being used to render the text
     * @param LINE_WIDTH the maximum length of a line as measured by text widths returned by {@code renderer}
     *                   must be greater than 0
     * @return           a {@link LiteralText} array of split lines
     */
    public static LiteralText [] split (LiteralText text, TextRenderer renderer, final int LINE_WIDTH) {
        if (LINE_WIDTH <= 0)
            throw new IllegalArgumentException ("Line width must be strictly greater than zero.");

        // Split the input text into words using any whitespace character as the
        // delimiter. To make things simpler, a space is appended as the 0th element.
        List <LiteralText> words = new ArrayList<> ();
        words.add (EMPTY_SPACE);
        words.addAll (Arrays.asList (divide (text)));

        final int N = words.size () - 1;

        // Initialize helper arrays for DP
        int [] wordWidths = new int [N + 1];
        for (ListIterator<LiteralText> iterator = words.listIterator (); iterator.hasNext ();) {
            wordWidths [iterator.nextIndex ()] = width (iterator.next (), renderer);
        }

        int [] table     = new int [N + 1];

        int [] backtrack = new int [N + 1];
        backtrack [N] = N;

        // Iterate over an increasing subset of the words starting at the first word, calculating the optimal
        // splittings for each subset.
        for (int i = 1; i <= N; ++ i) {
            int length = -1;
            table [i] = Integer.MAX_VALUE;

            for (int j = i; j <= N; ++ j) {
                length += wordWidths [j] + wordWidths [0];

                // If the line's overall length is less than the margin
                if (length < LINE_WIDTH) {
                    int penalty = (LINE_WIDTH - length) * (LINE_WIDTH - length);
                    // Then it can fit within, and we can calculate the associated cost.
                    int cost = (j == N) ? 0 : table[j + 1] + penalty;

                    // If this cost is less than the memoized cost then we found a more optimal splitting
                    if (cost < table[i]) {
                        table[i] = cost;
                        backtrack[i] = j;
                    }
                }
            }
        }

        // Backtrack step.
        List <LiteralText> out = new ArrayList<> ();

        // Starting at the beginning,
        int wordIndex = 1;
        // ... proceed through the backtracking table, reconstructing each line and appending it to the output.
        while (wordIndex <= N) {
            out.add (reconstruct (wordIndex, backtrack [wordIndex], words));
            wordIndex = backtrack [wordIndex] + 1;
        }

        return out.toArray (new LiteralText [0]);
    }

    private static LiteralText [] divide (LiteralText source) {
        if (! source.getRawString ().isEmpty ())
            throw new IllegalArgumentException ("LiteralText must be an empty parent.");

        List <LiteralText> ret = new ArrayList<> ();

        for (Text sibling : source.getSiblings ()) {
            String raw = sibling.asString ();
            String [] words = raw.split ("\\s");
            for (String word : words) {
                if (! word.isEmpty ())
                    ret.add ((LiteralText) new LiteralText (word.trim ()).setStyle (sibling.getStyle ()));
            }
        }

        return ret.toArray (new LiteralText [0]);
    }

    private static LiteralText reconstruct (int begin, int end, List <LiteralText> words) {
        assert begin <= end;

        ListIterator <LiteralText> iterator = words.listIterator (begin);
        Style currentStyle = null;
        String buffer = "";

        LiteralText out = new LiteralText ("");

        while (iterator.hasNext () && iterator.nextIndex () <= end) {
            LiteralText word = iterator.next ();
            if (currentStyle != word.getStyle ()) {
                if (! buffer.isEmpty ()) {
                    out.append (new LiteralText (buffer.trim ()).setStyle (currentStyle)).append (" ");
                    buffer = "";
                }
                currentStyle = word.getStyle ();
            }

            buffer = buffer.concat (word.getRawString () + " ");
        }

        if (! buffer.isEmpty ()) {
            out.append (new LiteralText (buffer.trim ()).setStyle (currentStyle));
        }

        return out;
    }

    /**
     * Divides the input list of {@code String}s into near-even sized groups of Strings, whose count is upper-bounded by
     * a user-provided limit.
     *
     * @param lines          The array of
     * @param LINES_PER_PAGE
     * @return               An array of {@code String} arrays. Each element is a group suitable for a
     *                       {@link ContentPage}.
     */
    public static LiteralText [][] pageify (List <LiteralText> lines, final int LINES_PER_PAGE) {
        List <LiteralText> buffer = new ArrayList<> ();
        List <LiteralText []> out = new ArrayList<> ();

        int linesExhausted = 0;
        boolean startedPage = false;
        boolean withholdEmptyLine = false;

        for (LiteralText line : lines) {
            // Skip
            if (line.getSiblings ().size () == 0 && ! startedPage)
                continue;

            startedPage = true;

            // If we are not ignoring any lines, and we encounter an empty line, then we need to ignore this one
            // and any subsequent empty lines
            boolean shouldWeHoldLine = MathHelper.implies (! withholdEmptyLine, line.getSiblings ().size () == 0);

            if (shouldWeHoldLine) {
                withholdEmptyLine = true;
            }

            // If we are withholding empty lines, and encounter a non-empty string, we want to add in this string
            // plus the empty string we withheld earlier. In essence, multiple empty lines get combined into one
            // single empty line, but they retain their page dividing effects.
            if (withholdEmptyLine && line.getSiblings ().size () > 0) {
                buffer.add (new LiteralText (""));
                buffer.add (line);
                withholdEmptyLine = false;
            }
            // Otherwise, just add the line if it's non-empty
            else if (line.getSiblings ().size () > 0) {
                buffer.add (line);
            }

            linesExhausted += 1;

            if (linesExhausted == LINES_PER_PAGE) {
                out.add (buffer.toArray (new LiteralText [0]));

                buffer.clear ();
                linesExhausted = 0;
                startedPage = false;
                withholdEmptyLine = false;
            }
        }

        if (buffer.size () > 0)
            out.add (buffer.toArray (new LiteralText [0]));

        return out.toArray (new LiteralText [out.size ()] []);
    }

    /**
     * Returns the width of the given content string, using the supplied {@link TextRenderer}.
     *
     * @param content  The content string
     * @param renderer The text renderer which will render this string.
     * @return         The width of the string in scaled pixels
     */
    public static int width (LiteralText content, TextRenderer renderer) {
        return renderer.getWidth (content);
    }

    /**
     * Parses the raw content string (as given by a datapack book JSON file) with Alchym's metadata syntax rules and
     * returns a renderable {@link LiteralText} representing that string.
     *
     * @param raw The content
     * @return    Its {@link LiteralText} representation
     */
    public static LiteralText parseString (String raw) {
        LiteralText parent = (LiteralText) (new LiteralText ("")).append (new LiteralText (raw));

        return parent;
    }
}
