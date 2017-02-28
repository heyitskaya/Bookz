package src.main.java.edu.mtholyoke.cs341bd.bookz;
import java.util.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Model{
	Map<String,GutenbergBook> library;
	Collection<GutenbergBook> books;

	public List<GutenbergBook> taggedBooks;

	public Model() throws IOException {
		// start with an empty hash-map; tell it it's going to be big in advance:
		library = new HashMap<>(40000);
		// do the hard work:
		books= library.values();
		DataImport.loadJSONBooks(library);
		taggedBooks = new ArrayList<GutenbergBook>();

	}

	public GutenbergBook getBook(String id) {
		return library.get(id);
	}
	
public void addTag(String bookID){
		
		GutenbergBook book = library.get(bookID) ; 
		if(book!= null){
		taggedBooks.add(library.get(bookID));
		}
		}

	public List<GutenbergBook> getBooksStartingWith(char firstChar) {
		// TODO, maybe it makes sense to not compute these every time.
		char query = Character.toUpperCase(firstChar);
		List<GutenbergBook> matches = new ArrayList<>(10000); // big
		for (GutenbergBook book : library.values()) {
			char first = Character.toUpperCase(book.title.charAt(0));
			if(first == query) {
				matches.add(book);
			}
		}
		return matches;
	}
	
	public List<GutenbergBook> getBooksWithString(String string){
		List<GutenbergBook> listToReturn= new ArrayList<GutenbergBook>();
		for(GutenbergBook currBook : library.values()) {
			String bookTitle=currBook.longTitle; //get the book title
			if(bookTitle.contains(string)){
				listToReturn.add(currBook);
			}
			//else do nothing
		}
		return listToReturn;
		
		
	}

	public List<GutenbergBook> getRandomBooks(int count) {
		return ReservoirSampler.take(count, library.values());
	}
	
	public List<GutenbergBook> getTaggedBooks(){
		return taggedBooks;
	}
}
