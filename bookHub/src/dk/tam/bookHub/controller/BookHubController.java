package dk.tam.bookHub.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import dk.tam.bookHub.dao.CommentDAO;
import dk.tam.bookHub.dao.CommentDAOImpl;
import dk.tam.bookHub.dao.ReviewDAO;
import dk.tam.bookHub.dao.ReviewDAOImpl;
import dk.tam.bookHub.model.Comments;
import dk.tam.bookHub.model.Reviews;

/**
 * @author Martyna
 *
 */
@MultipartConfig(fileSizeThreshold=1024*1024*2, // 2MB
maxFileSize=1024*1024*10,      // 10MB
maxRequestSize=1024*1024*50)   // 50MB
public class BookHubController extends HttpServlet {

	/**
	 * 
	 */
	public BookHubController() {
		// TODO Auto-generated constructor stub
		super();
	}
	
	public void init(ServletConfig config) throws ServletException {
		  super.init(config);
		  ReviewDAO reviewDAO = new ReviewDAOImpl();
		  List<Reviews> reviewList = reviewDAO.getAllReviews();	
		  ServletContext context = config.getServletContext();
		  context.setAttribute("reviewList", reviewList);
		 }
	
		 protected void doGet(HttpServletRequest request,
		   HttpServletResponse response) throws ServletException, IOException {
		   doPost(request, response); 
		 }
		 /**
			 * @param request Request contains the action we want to run, and the parameters to do so.
			 * @param response Response is used to contain the output of the request
			 */
			protected void doPost(HttpServletRequest request,
				HttpServletResponse response) throws ServletException, IOException {
				String base = "/jsp/";
				String url = base + "comments.jsp";
				String action = request.getParameter("action");
				//String category = request.getParameter("category");
				//String keyWord = request.getParameter("keyWord");
				if (action != null) {
					switch (action) {
					case "allComments":
						getAllComments(request, response);
						url = base + "comments.jsp";
						break;
					case "uploadImage":
						System.out.println("inside upload image case");
						uploadFileMethod(request, response);
						url = base + "index.jsp";
						break;
					}
				}
				RequestDispatcher requestDispatcher = getServletContext()
						.getRequestDispatcher(url);
				requestDispatcher.forward(request, response);
			}
			
			
			private void getAllComments(HttpServletRequest request,
					HttpServletResponse response) throws ServletException, IOException {
				try {
					CommentDAO commentDAO = new CommentDAOImpl();
					List<Comments> commentList = commentDAO.getAllComments();
					
					request.setAttribute("commentList", commentList);

				} catch (Exception e) {
					System.out.println(e);
				}
			}
			private static final String SAVE_DIR = "xxx";
			
			private void uploadFileMethod(HttpServletRequest request,
					HttpServletResponse response) throws ServletException, IOException {
				try {
					String appPath = request.getServletContext().getRealPath("");
			        // constructs path of the directory to save uploaded file
			        //String savePath = appPath + File.separator + SAVE_DIR;
					String savePath = appPath + SAVE_DIR;
			        System.out.println(appPath);
			        System.out.println(savePath);
			        
			        
			        // creates the save directory if it does not exists
			        File fileSaveDir = new File(savePath);
			        if (!fileSaveDir.exists()) {
			            fileSaveDir.mkdir();
			            System.out.println("makes file directory");
			        }
			         
			        //Part part = request.getPart("");
			        
			        
			        for (Part part : request.getParts()) {
			            String fileName = extractFileName(part);
			            String namesss = part.getContentType();
			            System.out.println(fileName);
			            System.out.println(namesss);
			            part.write(savePath + fileName);
			            System.out.println(savePath + fileName);
			        }
			 
			        request.setAttribute("message", "Upload has been done successfully!");

				} catch (Exception e) {
					System.out.println(e);
				}
			}
			
			private String extractFileName (Part part){
				String contentDisp = part.getHeader("content-disposition");
				String[] items = contentDisp.split(";");
				for(String s : items){
					if(s.trim().startsWith("filename")){
						
						return s.substring(s.indexOf("=")+2, s.length()-1);
					}
				}return "";
				}
}

