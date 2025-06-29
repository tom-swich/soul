package com.solvtrends.monolito.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
public class EmailService {

    @Value("${sendgrid.secret}")
    private String sendGridApiKey;

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String remetente;

    private String emailFrom = "suporte@solvtreds.com";

    private String assuntoResetPassword = "Reset Senha Solvtreds";
    private StringBuilder conteudoResetPassword = new StringBuilder("Reset Senha Solvtreds. \n Token para resetar senha: \n ");


    public void sendEmail(String email, String assunto, String conteudo) throws IOException {
        Email to = new Email(email);
        Email from = new Email(emailFrom);
        Content mailContent = new Content("text/plain", conteudo);

        Mail mail = new Mail(from, assunto, to, mailContent);

//        SendGrid sg = new SendGrid(sendGridApiKey);
        SendGrid sg = new SendGrid("solvtrends api key");
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException ex) {
            throw ex;
        }
    }

    public void sendEmailResetPassword(String nome, String email, int token) throws MessagingException {
        String conteudoHtml = montaCorpoEmailResetSenhaHmtl(nome, email, token);
        enviarEmailHtml(email, assuntoResetPassword, conteudoHtml);
    }

    public String enviarEmail(String destinatario, String assunto, String mensagem) {

        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(remetente);
            simpleMailMessage.setTo(destinatario);
            simpleMailMessage.setSubject(assunto);
            simpleMailMessage.setText(mensagem);
            javaMailSender.send(simpleMailMessage);
            return "Email enviado";
        }catch(Exception e) {
            return "Erro ao tentar enviar email " + e.getLocalizedMessage();
        }
    }

    public void enviarEmailHtml(String para, String assunto, String htmlCorpo) throws MessagingException {
        MimeMessage mensagem = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, "UTF-8");

        helper.setTo(para);
        helper.setSubject(assunto);
        helper.setText(htmlCorpo, true); // <- "true" ativa o modo HTML
        helper.setFrom(remetente);

        javaMailSender.send(mensagem);
    }

    public String montaCorpoEmailResetSenhaHmtl(String nome, String email, int token) {
        StringBuilder html = new StringBuilder("""
                    <html>
                        <body>
                            <h1 style="color: #2d90f5;">Olá, """ + nome + "! </h1>");
                        html.append(""" 
                            <p>Seu token de verificação é: <strong>""" + token + """ 
                            </strong></p>
                            <p>Este token expira em 5 minutos.</p>
                            <hr>
                            <p style="font-size: 12px; color: #aaa;">Mensagem automática - não responda</p>
                        </body>
                    </html>
                """);

        return html.toString();
    }
}
