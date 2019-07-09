package ru.skhatuntsev;

import com.rabbitmq.client.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;


public class Handler extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        final PrintWriter out = response.getWriter();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setVirtualHost("/");
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        Connection conn = null;
        boolean run = true;
        try {
            conn = factory.newConnection();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        Channel channel = null;
        if (conn != null) {
            channel = conn.createChannel();
        }
        String exchangeName = "myExchange";
        String queueName = "myQueue";
        String routingKey = "testRoute";
        boolean durable = true;
        if (channel != null) {
            channel.exchangeDeclare(exchangeName, "direct", durable);
        }
        if (channel != null) {
            channel.queueDeclare(queueName, durable, false, false, null);
        }
        if (channel != null) {
            channel.queueBind(queueName, exchangeName, routingKey);
        }
        out.println (

                "<!DOCTYPE html><html>" +
                        "<META HTTP-EQUIV=\"Refresh\" CONTENT=\"7\">" +
                        "<head>" +
                        "<meta charset=\"UTF-8\" />" +
                        "</head>" +
                        "<link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css\" integrity=\"sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T\" crossorigin=\"anonymous\">" +
                        "<body>" +
                        "<table border=1 align=center>");
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                       byte[] body) throws UnsupportedEncodingException {
                String message = new String(body, "UTF-8");
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add("<tr>" + "<td>" + "Consumer Received " + message + "</td>" + "</tr>");
                //out.print("<tr>" + "<td>" + "Consumer Received " + message + "</td>" + "</tr>");
                for (String i : arrayList) {
                    out.print(i);
               }
            }
            };
        if (channel != null) {
            channel.basicConsume(queueName, true, consumer);
        }
        try {
            if (channel != null) {
                channel.close();
            }
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        if (conn != null) {
            conn.close();
        }


        out.println(    "</table>" +
                        "<script src=\"https://code.jquery.com/jquery-3.3.1.slim.min.js\" integrity=\"sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo\" crossorigin=\"anonymous\"></script>" +
                        "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js\" integrity=\"sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1\" crossorigin=\"anonymous\"></script>" +
                        "<script src=\"https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js\" integrity=\"sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM\" crossorigin=\"anonymous\"></script>" +
                        "</body>" +
                        "</html>"
        );
    }
}