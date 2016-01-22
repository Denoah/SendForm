package tk.denoah.servlets;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;


@WebServlet(name = "Upload")
public class Upload extends HttpServlet {
    private Random random = new Random();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //проверяем является ли запрос multipart/form-data
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (!isMultipart) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }


        //создаем класс фабрику
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // Максимальный буфера данных в байтах,
        // при его привышении данные начнут записываться на диск во временную директорию
        // устанавливаем один мегабайт
        factory.setSizeThreshold(1024*1024);

        //Устанавливаем временную директорию
        File tempDir = (File) getServletContext().getAttribute("javax.servlet.context.tempdir");
        factory.setRepository(tempDir);

        //Создаем загрузчик
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setSizeMax(1024*1024*10);

        try {
            List items = upload.parseRequest(request);
            Iterator iter = items.iterator();

            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();

                if (item.isFormField()) {
                    response.getWriter().write("go!");
                    processFormField(item);
                    response.getWriter().write("ok");
                } else {
                    response.getWriter().write("fileupload");
                    File uploadFile = null;
                    String path = item.getName();
                    response.getWriter().write(path);

                    uploadFile = new File("C://output/", path);
                    boolean created = uploadFile.createNewFile();
                    item.write(uploadFile);


                    if (uploadFile.exists()) {
                        response.getWriter().write("File Created");
                    }



                }
            }
        } catch (FileUploadException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void processFormField(FileItem item) {
        System.out.println(item.getFieldName()+"="+item.getString());
    }

    private String processUploadFile(FileItem item) throws Exception{
        File uploadFile = null;
        String path = null;
        do{
            path = getServletContext().getRealPath("C://"+random.nextInt()+ item.getName());
            uploadFile = new File(path);
        } while (uploadFile.exists());
        uploadFile.createNewFile();
        item.write(uploadFile);
        return path;
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
