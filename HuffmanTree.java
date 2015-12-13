import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Scanner;

public class HuffmanTree {

	private static class HuffmanTreeNode implements Comparable<HuffmanTreeNode> {
		public Integer data, freq;
		public HuffmanTreeNode left, right;

		public HuffmanTreeNode() {
		}

		public HuffmanTreeNode(int freq, int data) {
			this.data = new Integer(data);
			this.freq = new Integer(freq);
		}

		public HuffmanTreeNode(HuffmanTreeNode left, HuffmanTreeNode right) {
			this.left = left;
			this.right = right;
			this.freq = new Integer(left.freq + right.freq);
		}

		@Override
		public int compareTo(HuffmanTreeNode o) {
			return (int) Math.signum(freq - o.freq);
		}
	}

	private HuffmanTreeNode overallRoot;

	private static String subTreetoString(HuffmanTreeNode root, String route) {
		if (root == null)
			return "";
		return root.data != null ? root.data.toString() + "\n" + route
				: subTreetoString(root.left, route + "0") + "\n" + subTreetoString(root.right, route + "1");
	}

	private static void subTreeAddNode(HuffmanTreeNode root, String route, String ascii_code) {
		if (root == null)
			return;
		if (route.isEmpty())
			root.data = Integer.parseInt(ascii_code);
		else {
			HuffmanTreeNode next;
			if (route.charAt(0) == '0') {
				if (root.left == null)
					root.left = new HuffmanTreeNode();
				next = root.left;
			} else {
				if (root.right == null)
					root.right = new HuffmanTreeNode();
				next = root.right;
			}
			subTreeAddNode(next, route.substring(1), ascii_code);
		}
	}

	// construct from counts array
	public HuffmanTree(int[] counts) {
		PriorityQueue<HuffmanTreeNode> q = new PriorityQueue<HuffmanTreeNode>();
		for (int ascii_code = 0; ascii_code < counts.length; ascii_code++)
			if (counts[ascii_code] != 0)
				q.offer(new HuffmanTreeNode(counts[ascii_code], ascii_code));

		q.offer(new HuffmanTreeNode(1, counts.length));

		while (q.size() != 1)
			q.offer(new HuffmanTreeNode(q.remove(), q.remove()));
		overallRoot = q.remove();
	}

	// construct from code file
	public HuffmanTree(Scanner input) {
		overallRoot = new HuffmanTreeNode();
		String ascii_code, route;
		try {
			while ((ascii_code = input.nextLine()) != null && (route = input.nextLine()) != null)
				subTreeAddNode(overallRoot, route, ascii_code);
		} catch (NoSuchElementException e) {
			return;
		}
	}

	public void write(PrintStream out) {
		out.print(this);
	}

	public String toString() {
		return subTreetoString(overallRoot, new String());
	}

	public void decode(BitInputStream input, PrintStream output, int eof) {
		HuffmanTreeNode cur = overallRoot;
		int b;
		while ((cur.data == null || cur.data != eof) && (b = input.readBit()) != -1) {
			if (cur.data != null) {
				output.write(cur.data);
				cur = overallRoot;
			}
			cur = (b == 0) ? cur.left : cur.right;
		}
	}
}
