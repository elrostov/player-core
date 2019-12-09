package spring.app.service.impl;

import org.springframework.stereotype.Service;
import spring.app.service.abstraction.MusicService;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Service
public class MusicServiceImpl implements MusicService {
    @Override
    public ServletOutputStream fileToStream(String musicName, HttpServletResponse response) throws ServletException, IOException {
        String filePath = musicName + ".mp3";

        ServletOutputStream stream = null;
        BufferedInputStream buf = null;
        try {

            File mp3 = new File("C:\\Users\\g\\IdeaProjects\\player-core\\src\\main\\resources\\" + filePath);

            //set response headers
            stream = response.getOutputStream();
            response.setContentType("audio/mpeg");

            response.addHeader("Content-Disposition", "attachment; filename=" + filePath);

            response.setContentLength((int) mp3.length());

            FileInputStream input = new FileInputStream(mp3);
            buf = new BufferedInputStream(input);
            int readBytes = 0;
            //read from the file; write to the ServletOutputStream
            while ((readBytes = buf.read()) != -1)
                stream.write(readBytes);
            return stream;
        } catch (IOException ioe) {
            throw new ServletException(ioe.getMessage());
        } finally {
            if (stream != null)
                stream.close();
            if (buf != null)
                buf.close();
        }
    }
}
