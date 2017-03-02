package src.main.java.edu.mtholyoke.cs341bd.bookz;
import java.util.*;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

//import edu.mtholyoke.cs341bd.writr.WritrPost;
//import edu.mtholyoke.cs341bd.writr.WritrView;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * @author jfoley
 */
public class BookzServer extends AbstractHandler {
	Server jettyServer;
	HTMLView view;
	Model model;
	String titleSearched;
	int numPages;
	//List<GutenbergBook> booksReturned;
	int counter;


	public BookzServer(String baseURL, int port) throws IOException {
		view = new HTMLView(baseURL);
		jettyServer = new Server(port);
		model = new Model();
		
		

		// We create a ContextHandler, since it will catch requests for us under
		// a specific path.
		// This is so that we can delegate to Jetty's default ResourceHandler to
		// serve static files, e.g. CSS & images.
		ContextHandler staticCtx = new ContextHandler();
		staticCtx.setContextPath("/static");
		ResourceHandler resources = new ResourceHandler();
		resources.setBaseResource(Resource.newResource("static/"));
		staticCtx.setHandler(resources);

		// This context handler just points to the "handle" method of this
		// class.
		ContextHandler defaultCtx = new ContextHandler();
		defaultCtx.setContextPath("/");
		defaultCtx.setHandler(this);

		// Tell Jetty to use these handlers in the following order:
		ContextHandlerCollection collection = new ContextHandlerCollection();
		collection.addHandler(staticCtx);
		collection.addHandler(defaultCtx);
		jettyServer.setHandler(collection);
		
	}

	/**
	 * Once everything is set up in the constructor, actually start the server
	 * here:
	 * 
	 * @throws Exception
	 *             if something goes wrong.
	 */
	public void run() throws Exception {
		jettyServer.start();
		jettyServer.join(); // wait for it to finish here! We're using threads behind the scenes; so this keeps the main thread around until something can happen!
	}

	/**
	 * The main callback from Jetty.
	 * 
	 * @param resource
	 *            what is the user asking for from the server?
	 * @param jettyReq
	 *            the same object as the next argument, req, just cast to a
	 *            jetty-specific class (we don't need it).
	 * @param req
	 *            http request object -- has information from the user.
	 * @param resp
	 *            http response object -- where we respond to the user.
	 * @throws IOException
	 *             -- If the user hangs up on us while we're writing back or
	 *             gave us a half-request.
	 * @throws ServletException
	 *             -- If we ask for something that's not there, this might
	 *             happen.
	 */
	//everything you request to server is a request 
	//sort=author
	//p=13
	//cat=dog are all in the parameterMap
	//so we can access page number by key "p"
	@Override
	public void handle(String resource, Request jettyReq, HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		System.out.println(jettyReq);

		String method = req.getMethod();
		String path = req.getPathInfo();
	
		
		
		//added 
		
		//PrintWriter html = resp.getWriter();
		//System.out.println("print search form");
	    //HTMLView.printSearchForm(html);
		

		if ("GET".equals(method)) {
			if("/robots.txt".equals(path)) {
				// We're returning a fake file? Here's why: http://www.robotstxt.org/
				resp.setContentType("text/plain");
				try (PrintWriter txt = resp.getWriter()) {
					txt.println("User-Agent: *");
					txt.println("Disallow: /");
				}
				return;
			}

			String id = Util.getAfterIfStartsWith("/tagBook/", path);
			if(id != null) {
				System.out.println("id" + id);
				model.addTag(id);
				counter++;
				System.out.println(counter);
				view.showFrontPage(model, resp);
			}

			String tag = Util.getAfterIfStartsWith("/review/", path);
			if(tag!= null ){
				view.showTaggedBooks(model.taggedBooks, resp);
			}

			
			if("/submit".equals(path)) {
				Map<String, String[]> allInputs = req.getParameterMap();
				titleSearched = req.getParameter("title");
				req.getParameter("page"); //getting the page number
				List<GutenbergBook> booksReturned=this.model.getBooksWithString(titleSearched);
				System.out.println("numBooks returned "+booksReturned.size());
				if(booksReturned.size()%10!=0)
				{
					numPages=booksReturned.size()/10+1;
				}
				else{
					numPages=booksReturned.size()/10;
				}
				System.out.println("numPages "+numPages);
				
				
				view.printPaging(resp.getWriter(),numPages,titleSearched);
				//System.out.println("showBookCollection pageNumber "+allInputs.get("page").toString());
				int currPageNumber;
				if(Util.join(allInputs.get("page"))==null){
					view.showBookCollection(booksReturned,resp,1);
					System.out.println("We are displaying the first page");
				}
				else
				{
					currPageNumber =Integer.parseInt(Util.join(allInputs.get("page")));
					view.showBookCollection(booksReturned,resp, currPageNumber);
					System.out.println("displaying collection of books from "+currPageNumber);
					
				}
						
				
				
			}
			
			String titleCmd = Util.getAfterIfStartsWith("/title/", path);
			if(titleCmd != null) {
				char firstChar = titleCmd.charAt(0);
				view.showBookCollection(this.model.getBooksStartingWith(firstChar), resp,1); //I made this up
			}

			// Check for startsWith and substring
			String bookId = Util.getAfterIfStartsWith("/book/", path);
			if(bookId != null) {
				view.showBookPage(this.model.getBook(bookId), resp);
			}

			// Front page!
			if ("/front".equals(path) || "/".equals(path)) {
				view.showFrontPage(this.model, resp);
				return;
			}
		}
		
		//added part
		try (PrintWriter html = resp.getWriter()) {
	    	
		    //  WritrView.printWritrPageStart(html, "Writr", metaURL, getStaticURL
		      //    ("writr.css"));

		      // Print the form at the top of the page
		
		      // Print all of our messages
		      html.println("<div class=\"body\">");

		      // get a copy to sort:
		      /**
		      ArrayList<Integer> messages = new ArrayList<>(model.getPosts().keySet());
		      Collections.sort(messages);
		      Collections.reverse(messages);

		      StringBuilder messageHTML = new StringBuilder();
		      for (int postId : messages) {
		        WritrPost writrPost = model.getPost(postId);
		        WritrView.displayPost(messageHTML, writrPost);
		      }
		      html.println(messageHTML);
		      html.println("</div>");

		      // when we have a big page,
		      if (messages.size() > 25) {
		        // Print the submission form again at the bottom of the page
		        WritrView.printWritrForm(html);
		      }
		     
		      WritrView.printWritrPageEnd(html);
		    	
		    	**/
		    }
	}

	public void handleTaggedPage(String resource, Request jettyReq, HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		System.out.println(jettyReq);

		String method = req.getMethod();
		String path = req.getPathInfo();

}
}