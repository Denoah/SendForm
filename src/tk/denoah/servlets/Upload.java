package tk.denoah.servlets;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;


@WebServlet(name = "Upload")
public class Upload extends HttpServlet {

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
        factory.setSizeThreshold(1024 * 1024);

        //Устанавливаем временную директорию
        File tempDir = (File) getServletContext().getAttribute("javax.servlet.context.tempdir");
        factory.setRepository(tempDir);

        String dir = "C://output/";

        //Создаем загрузчик
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setSizeMax(1024 * 1024 * 10);

        try {
            List items = upload.parseRequest(request);
            Iterator iter = items.iterator();

            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();

                if (item.isFormField()) {
                    processFormField(item);
                } else {
                    File uploadFile = null;
                    String path = item.getName();
                    response.getWriter().write(path);
                    uploadFile = new File(dir, path);
                    uploadFile.createNewFile();
                    String nodeString = parseMyXMLFile(uploadFile);

                    if (uploadFile.exists()) {
                        response.getWriter().write(nodeString);
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
        System.out.println(item.getFieldName() + "=" + item.getString());
    }

    private String parseMyXMLFile(File file) throws ParserConfigurationException, IOException, SAXException {
        Node resultNode = null;
        String s = "";

        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setValidating(false);
        DocumentBuilder builder = f.newDocumentBuilder();
        Document doc = builder.parse(file);
        doc.getDocumentElement().normalize();

        //Тут индусятина но времени нет, исправить как будет время
        NodeList list = doc.getChildNodes();
        Node childNode = list.item(1);
        NodeList list1 = childNode.getChildNodes();
        Node childNode1 = list1.item(0);
        NodeList list2 = childNode1.getChildNodes();
        Node childNode2 = list2.item(0);
        NodeList list3 = childNode2.getChildNodes();
        Node childNode3 = list3.item(1);
        NodeList nodeList = childNode3.getChildNodes();
        //конец индусятины

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node children = nodeList.item(i);
            resultNode = children.getAttributes().item(0);
            s += ";" + resultNode.getTextContent();
        }

        return s;
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
