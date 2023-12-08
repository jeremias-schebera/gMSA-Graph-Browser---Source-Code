//package main.Servlet;
//
//import java.io.*;
//import javax.servlet.*;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.*;
//
//@WebServlet("/upload")
//public class UploadServlet extends HttpServlet {
//   @Override
//   public void doGet(HttpServletRequest request, HttpServletResponse response)
//               throws IOException, ServletException {
//      // Set the response message's MIME type
//      response.setContentType("text/html;charset=UTF-8");
//      // Allocate a output writer to write the response message into the network socket
//      PrintWriter out = response.getWriter();
// 
//      // Write the response message, in an HTML page
//      try {
//         out.println("<!DOCTYPE html>");
//         out.println("<html><head>");
//         out.println("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
//         out.println("<title>Hello, World</title></head>");
//         out.println("<body>");
//         out.println("<h1>Hello, world!</h1>");  // says Hello
//         // Echo client's request information
//         out.println("<p>Request URI: " + request.getRequestURI() + "</p>");
//         out.println("<p>Protocol: " + request.getProtocol() + "</p>");
//         out.println("<p>PathInfo: " + request.getPathInfo() + "</p>");
//         out.println("<p>Remote Address: " + request.getRemoteAddr() + "</p>");
//         // Generate a random number upon each request
//         out.println("<p>A Random Number: <strong>" + Math.random() + "</strong></p>");
//         out.println("</body>");
//         out.println("</html>");
//      } finally {
//         out.close();  // Always close the output writer
//      }
//   }
//}

package main.Servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import main.Controller.ControlWindowController;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 *
 * This is a servlet demo,  for using Resumable.js to upload files.
 *
 * by fanxu123
 */
@WebServlet("/upload")
public class UploadServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int resumableChunkNumber        = getResumableChunkNumber(request);

        ResumableInfo info = getResumableInfo(request);

        RandomAccessFile raf = new RandomAccessFile(info.resumableFilePath, "rw");

        //Seek to position
        raf.seek((resumableChunkNumber - 1) * (long)info.resumableChunkSize);

        //Save to file
        InputStream is = request.getInputStream();
        long readed = 0;
        long content_length = request.getContentLength();
        byte[] bytes = new byte[1024 * 100];
        while(readed < content_length) {
            int r = is.read(bytes);
            if (r < 0)  {
                break;
            }
            raf.write(bytes, 0, r);
            readed += r;
        }
        raf.close();


        //Mark as uploaded.
        info.uploadedChunks.add(new ResumableInfo.ResumableChunkNumber(resumableChunkNumber));
        if (info.checkIfUploadFinished()) { //Check if all chunks uploaded, and change filename
            ResumableInfoStorage.getInstance().remove(info);
            response.getWriter().print("All finished.");
        } else {
            response.getWriter().print("Upload");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int resumableChunkNumber        = getResumableChunkNumber(request);

        ResumableInfo info = getResumableInfo(request);

        if (info.uploadedChunks.contains(new ResumableInfo.ResumableChunkNumber(resumableChunkNumber))) {
            response.getWriter().print("Uploaded."); //This Chunk has been Uploaded.
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private int getResumableChunkNumber(HttpServletRequest request) {
        return HttpUtils.toInt(request.getParameter("resumableChunkNumber"), -1);
    }

    private ResumableInfo getResumableInfo(HttpServletRequest request) throws ServletException {
        String base_dir = UPLOAD_DIR;

        int resumableChunkSize          = HttpUtils.toInt(request.getParameter("resumableChunkSize"), -1);
        long resumableTotalSize         = HttpUtils.toLong(request.getParameter("resumableTotalSize"), -1);
        String resumableIdentifier      = request.getParameter("resumableIdentifier");
        String resumableFilename        = request.getParameter("resumableFilename");
        String resumableRelativePath    = request.getParameter("resumableRelativePath");
        //Here we add a ".temp" to every upload file to indicate NON-FINISHED
        new File(base_dir).mkdir();
        String resumableFilePath        = new File(base_dir, resumableFilename).getAbsolutePath() + ".temp";

        ResumableInfoStorage storage = ResumableInfoStorage.getInstance();

        ResumableInfo info = storage.get(resumableChunkSize, resumableTotalSize,
                resumableIdentifier, resumableFilename, resumableRelativePath, resumableFilePath);
        if (!info.vaild())         {
            storage.remove(info);
            throw new ServletException("Invalid request params.");
        }
        return info;
    }
}
