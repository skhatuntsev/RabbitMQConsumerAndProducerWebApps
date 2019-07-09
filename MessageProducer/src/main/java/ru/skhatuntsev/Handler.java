package ru.skhatuntsev;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeoutException;

public class Handler extends HttpServlet {
    @Override
    public void init() {
        try {
            MessageProducer.main(new String [0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
