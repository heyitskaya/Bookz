package src.main.java.edu.mtholyoke.cs341bd.bookz;
import java.util.*;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class HTMLView {

	private String metaURL;
	public int resultsPerPage=10;

	public HTMLView(String baseURL) {
		this.metaURL = "<base href=\"" + baseURL + "\">";
	}

	/**
	 * HTML top boilerplate; put in a function so that I can use it for all the
	 * pages I come up with.
	 * 
	 * @param html
	 *            where to write to; get this from the HTTP response.
	 * @param title
	 *            the title of the page, since that goes in the header.
	 */
	void printPageStart(PrintWriter html, String title) {
		html.println("<!DOCTYPE html>"); // HTML5
		html.println("<html>");
		html.println("  <head>");
		html.println("    <title>" + title + "</title>");
		html.println("    " + metaURL);
		html.println("    <link type=\"text/css\" rel=\"stylesheet\" href=\"" + getStaticURL("bookz.css") + "\">");
		html.println("  </head>");
		html.println("  <body>");
		html.println("  <a href='/front'><h1 class=\"logo\">"+title+"</h1></a>");
	}
	
	
	
	
	public static void printSearchForm(PrintWriter output) {
	    output.println("<div class=\"form\">");
	    output.println("  <form action=\"submit\" method=\"GET\">"); //changed to get
	   output.println(" <label><br>Title: <input type=\"text\" name=\"title\" " +
	        "/></label>");
	   output.println("     <br><input type=\"submit\" value=\"Search!\" />");
	    output.println("  </form>");
	    output.println("</div>");
	  }
	

	public String getStaticURL(String resource) {
		return "static/" + resource;
	}
	
	public static void printPaging(PrintWriter output,int numPages, String titleSearched){
		int page=1;
		for(page = 1; page <= numPages; page++) {
			Map<String, String> m= new HashMap<String, String>();
			m.put("title",titleSearched );
			m.put("page",Integer.toString(page)); //put these in the map
			String newURL=Util.encodeParametersInURL(m,"/title");
			output.println("<a href='/title/"+page+"'>"+page+"</a> "); //maybe
		}
		
	}

	/**
	 * HTML bottom boilerplate; close all the tags we open in
	 * printPageStart.
	 *
	 * @param html
	 *            where to write to; get this from the HTTP response.
	 */
	void printPageEnd(PrintWriter html) {
		html.println("  </body>");
		html.println("</html>");
	}
	

	void showFrontPage(Model model, HttpServletResponse resp) throws IOException {
		try (PrintWriter html = resp.getWriter()) {
			printPageStart(html, "Bookz");
			
			System.out.println("print search form");
		    HTMLView.printSearchForm(html);
		    

			html.println("<h3>Browse books by title</h3>");

			for(char letter = 'A'; letter <= 'Z'; letter++) {
				html.println("<a href='/title/"+letter+"'>"+letter+"</a> ");
			}

			// get 5 random books:
			html.println("<h3>Check out these random books</h3>");
			List<GutenbergBook> randomBooks = model.getRandomBooks(5);
			for (GutenbergBook randomBook : randomBooks) {
				printBookHTML(html, randomBook);
			}
			printPageEnd(html);
		}
	}

	public void showBookPage(GutenbergBook book, HttpServletResponse resp) throws IOException {
		try (PrintWriter html = resp.getWriter()) {
			printPageStart(html, "Bookz");
			printBookHTML(html, book);
			printPageEnd(html);
		}
	}

	private void printBookHTML(PrintWriter html, GutenbergBook book) {
		html.println("<div class='book'>");
		html.println("<a class='none' href='/book/"+book.id+"'>");
		html.println("<div class='title'>"+book.longTitle+"</div>");
		if(book.creator != null) {
			html.println("<div class='creator'>" + book.creator + "</div>");
		}
		html.println("<a href='"+book.getGutenbergURL()+"'>On Project Gutenberg</a>");
		// TODO, finish up fields.
		html.println("</a>");
		html.println("</div>");
	}
	
	

	public void showBookCollection(List<GutenbergBook> theBooks, HttpServletResponse resp) throws IOException {
		try (PrintWriter html = resp.getWriter()) {
			printPageStart(html, "Bookz");

			for (int i = 0; i < Math.min(20,theBooks.size()); i++) {
				printBookHTML(html, theBooks.get(i));
			}

			printPageEnd(html);
		}
	}
}
