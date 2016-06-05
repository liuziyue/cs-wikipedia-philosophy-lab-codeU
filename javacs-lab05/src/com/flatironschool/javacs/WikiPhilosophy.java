package com.flatironschool.javacs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import org.jsoup.select.Elements;

public class WikiPhilosophy {
	
	final static WikiFetcher wf = new WikiFetcher();
	
	/**
	 * Tests a conjecture about Wikipedia and Philosophy.
	 * 
	 * https://en.wikipedia.org/wiki/Wikipedia:Getting_to_Philosophy
	 * 
	 * 1. Clicking on the first non-parenthesized, non-italicized link
     * 2. Ignoring external links, links to the current page, or red links
     * 3. Stopping when reaching "Philosophy", a page with no links or a page
     *    that does not exist, or when a loop occurs
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		
        // some example code to get you started
        List<String> pagesVisited = new ArrayList<String>();

		String url = "https://en.wikipedia.org/wiki/Java_(programming_language)";
		String target = "https://en.wikipedia.org/wiki/Philosophy";
		boolean isLink = false;
		while (!url.equals(target)) {
			Elements paragraphs = wf.fetchWikipedia(url);
			pagesVisited.add(url);
			int parenthesis = 0;
			for (Element firstPara : paragraphs) {

				Iterable<Node> iter = new WikiNodeIterable(firstPara);
				for (Node node: iter) {
					if (node instanceof TextNode) {
						if (((TextNode) node).text().contains("(")) {
							parenthesis++;
						} else if (((TextNode) node).text().contains(")")) {
							parenthesis--;
						}
					} else if (node instanceof Element) {
						String absUrl = ((Element) node).absUrl("href");
						Element e = (Element) node;
						if (e.tagName() == "a") {
							// found a link 
							isLink = true;
							// Check if it contains italics
							for (Element ele : e.parents()) {
								if (ele.tagName().equals("i") || ele.tagName().equals("em")) {
									isLink = false;
								}
							}
							// If parenthesis is not valid, the link is not valid
							if (parenthesis != 0) {
								isLink = false;
							}

							// Find next
							String next_url = "https://en.wikipedia.org" + node.attr("href");
							int end = next_url.indexOf('#');
							if (end != -1 || pagesVisited.contains(next_url)) {
								isLink = false;
							}

							if (isLink) {
								url = next_url;
								break;
							}

						}

					}

				}
				if (isLink) {
					break;
				}
			}
			if (!isLink) {
				System.out.println("Link not found");
				break;
			}

		}

        // // the following throws an exception so the test fails
        // // until you update the code
        // String msg = "Complete this lab by adding your code and removing this statement.";
        // throw new UnsupportedOperationException(msg);
	}

}
