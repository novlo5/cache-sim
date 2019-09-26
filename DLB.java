import java.io.*;
import java.util.Scanner;

public class DLB {
    private final int MAX_PREDICTIONS = 5;
    private DLBNode root;
    private final static Character emptyfield = '.';

    public DLB() {
        root = new DLBNode(emptyfield);
    }

    public void addWord(String s) {
        if (s == null) {
            return;
        }
        DLBNode curr = root;
        String fullString = "";
        for (int i = 0; i < s.length(); i++) // use this loop and an addhelper for entry into trie
        {
            fullString += s.charAt(i);
            curr = addhelper(s.charAt(i), curr, i, s.length());
        }
        curr.setFullWord(true);
        curr.setFullString(fullString);
    }

    public DLBNode addhelper(Character c, DLBNode curr, int i, int length) {
        if (curr.getKey().equals(emptyfield)) {
            curr.setKey(c);
            curr.setChild(new DLBNode(emptyfield));
            if (i != (length - 1)) {
                curr = curr.getChild();
            }
        } else if (curr.getKey().equals(c)) {
            curr = curr.getChild();
        } else {
            if (curr.getSibling() == null) {
                curr.setSibling(new DLBNode(emptyfield));
            }
            curr = addhelper(c, curr.getSibling(), i, length);
        }
        return curr;
    }

    public int keysWithPrefix(String userinput, String[] predictionsArray, int length) {
        DLBNode curr = root;
        String testString = "";
        int i = 0;
        while (i < userinput.length()) {
            if (curr.getKey().equals(userinput.charAt(i))) {
                testString += curr.getKey();
                if (i != userinput.length() - 1) {
                    curr = curr.getChild();
                }
                ++i;
            } else if (curr.getSibling() != null) {
                curr = curr.getSibling();
            }
        }
        if (testString.equals(userinput)) {
            length = finishpredictions(curr, predictionsArray, length);
        }
        return length;
    }

    private int finishpredictions(DLBNode curr, String[] possiblewords, int length) {
        if (length < MAX_PREDICTIONS && curr.getKey() != emptyfield) {
            if (curr.isFullWord()) {
                possiblewords[length] = curr.getFullString();
                length += 1;
            }
            if (curr.getChild() != null) {
                length = finishpredictions(curr.getChild(), possiblewords, length);
                if (length == MAX_PREDICTIONS) {
                    return length;
                }
            }
            if (curr.getSibling() != null) {
                length = finishpredictions(curr.getSibling(), possiblewords, length);
                if (length == MAX_PREDICTIONS) {
                    return length;
                }
            }
			return length;
        }
        return length;
    }
	public boolean empty(){return root.getKey().equals(emptyfield);}

    private static class DLBNode {

        private Character key;
        private DLBNode sibling;
        private DLBNode child;
        private boolean fullWord;
        private String fullstring;

        private DLBNode(Character key) {
            this(key, null, null, false, null);
        }

        private DLBNode(Character key, DLBNode sibling, DLBNode child, boolean isWord, String fullstring) {
            setKey(key);
            setSibling(sibling);
            setChild(child);
            setFullWord(false);
        }

        public void setKey(Character key) {
            this.key = key;
        }

        public void setSibling(DLBNode sibling) {
            this.sibling = sibling;
        }

        public void setChild(DLBNode child) {
            this.child = child;
        }

        private void setFullWord(boolean isWord) {
            this.fullWord = isWord;
        }

        private void setFullString(String s) {
            this.fullstring = s;
        }

        public Character getKey() {
            return this.key;
        }

        public DLBNode getSibling() {
            return this.sibling;
        }

        public DLBNode getChild() {
            return this.child;
        }

        private boolean isFullWord() {
            return this.fullWord;
        }

        private String getFullString() {
            return this.fullstring;
        }

    }

}
