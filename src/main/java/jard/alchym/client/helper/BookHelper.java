package jard.alchym.client.helper;

import jard.alchym.api.book.impl.ContentPage;
import jard.alchym.helper.MathHelper;
import net.minecraft.client.font.TextRenderer;

import java.util.*;

/***
 *  BookHelper
 *  Contains various helper methods for book content processing and rendering.
 *
 *  Created by jard at 20:07 on January, 01, 2021.
 ***/
public class BookHelper {
    /**
     * Splits the input {@code text} into a word-wrapped array of strings, suitable for rendering.
     * Minimizes the sum of the squared widths of whitespace at the end of each line, returning a block
     * of text with minimal raggedness.
     *
     * @param text       input text to split
     * @param renderer   the {@link TextRenderer} being used to render the text
     * @param LINE_WIDTH the maximum length of a line as measured by text widths returned by {@code renderer}
     *                   must be greater than 0
     * @return           a {@link String} array of split lines
     */
    public static String [] split (String text, TextRenderer renderer, final int LINE_WIDTH) {
        if (LINE_WIDTH <= 0)
            throw new IllegalArgumentException ("Line width must be strictly greater than zero.");

        // Split the input text into words using any whitespace character as the
        // delimiter. To make things simpler, a space is appended as the 0th element.
        List <String> words = new ArrayList<> ();
        words.add (" ");
        words.addAll (Arrays.asList (text.split ("\\s")));

        final int N = words.size () - 1;

        // Initialize helper arrays for DP
        int [] wordWidths = new int [N + 1];
        for (ListIterator<String> iterator = words.listIterator (); iterator.hasNext ();) {
            wordWidths [iterator.nextIndex ()] = renderer.getWidth (iterator.next ());
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
        List <String> out = new ArrayList<> ();

        // Starting at the beginning,
        int wordIndex = 1;
        // ... proceed through the backtracking table, reconstructing each line and appending it to the output.
        while (wordIndex <= N) {
            out.add (reconstruct (wordIndex, backtrack [wordIndex], words));
            wordIndex = backtrack [wordIndex] + 1;
        }

        return out.toArray (new String [0]);
    }

    private static String reconstruct (int begin, int end, List <String> words) {
        assert begin <= end;

        ListIterator <String> iterator = words.listIterator (begin);
        String out = "";

        while (iterator.hasNext () && iterator.nextIndex () <= end) {
            out = out.concat (iterator.next () + " ");
        }

        return out.trim ();
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
    public static String [][] pageify (List <String> lines, final int LINES_PER_PAGE) {
        List <String> buffer = new ArrayList<> ();
        List <String []> out = new ArrayList<> ();

        int linesExhausted = 0;
        boolean startedPage = false;
        boolean withholdEmptyLine = false;

        for (String line : lines) {
            // Skip
            if (line.isEmpty () && ! startedPage)
                continue;

            startedPage = true;

            // If we are not ignoring any lines, and we encounter an empty line, then we need to ignore this one
            // and any subsequent empty lines
            boolean shouldWeHoldLine = MathHelper.implies (! withholdEmptyLine, line.isEmpty ());

            if (shouldWeHoldLine) {
                withholdEmptyLine = true;
            }

            // If we are withholding empty lines, and encounter a non-empty string, we want to add in this string
            // plus the empty string we withheld earlier. In essence, multiple empty line get combined into one
            // single empty line, but they retain their page dividing effects.
            if (withholdEmptyLine && ! line.isEmpty ()){
                buffer.add ("");
                buffer.add (line);
                withholdEmptyLine = false;
            }
            // Otherwise, just add the line if it's non-empty
            else if (! line.isEmpty ()) {
                buffer.add (line);
            }

            linesExhausted += height (line);

            if (linesExhausted == LINES_PER_PAGE) {
                out.add (buffer.toArray (new String [0]));

                buffer.clear ();
                linesExhausted = 0;
                startedPage = false;
                withholdEmptyLine = false;
            }
        }

        if (buffer.size () > 0)
            out.add (buffer.toArray (new String [0]));

        return out.toArray (new String [out.size ()] []);
    }

    /**
     * Returns the number of lines occupied by the given content string.
     *
     * @param string The content string
     * @return       The number of lines
     */
    public static int height (String string) {
        // TODO: Implement other types of content strings. Body text has 1 line per string, but other types will have more
        return 1;
    }
}
