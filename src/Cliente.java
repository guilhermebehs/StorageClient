
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Cliente {
 
     private Socket socketServidor;
     private OutputStream saida;
     private InputStream entrada;    
     
    public int enviarArquivo(File file){
        
        int id = 0;
        
         try {
             socketServidor = new Socket("localhost", 8090);
             entrada = socketServidor.getInputStream();
             saida = socketServidor.getOutputStream();
             byte[] buf = Files.readAllBytes(file.toPath());
             saida.write(Operacao.ENVIAR_BYTES.valor);
             id = entrada.read();
             saida.write(buf);
             entrada.read();
             socketServidor.close();
         } catch (IOException ex) {
             ex.printStackTrace();
         }
        
         return id;
        
    }
    
    public void retornarArquivo(int id, String nomeArquivo){
                
         try {
             socketServidor = new Socket("localhost", 8090);
             entrada = socketServidor.getInputStream();
             saida = socketServidor.getOutputStream();
             saida.write(Operacao.RETORNAR_BYTES.valor);
             saida.write(id);
             
             //Aguarda o sinal de que já pode receber os bytes
              entrada.read();
              Thread.sleep(2000);
              
             byte[] bytesFinal =   retornaArquivoEmBytes(entrada);
             FileOutputStream out = new FileOutputStream(nomeArquivo);
             out.write(bytesFinal);
             out.close();
             
         } catch (IOException ex) {
            ex.printStackTrace();
         } catch (InterruptedException ex) {
             Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
         }
        
    }
    
    
    public byte[] retornaArquivoEmBytes(InputStream entrada) {

        List<byte[]> bytesList = new ArrayList();
        byte[] bytesFinal = new byte[1024];
        int lengthFinal = 0;
        try {
            while (entrada.available() > 0) {
                lengthFinal += entrada.available();
                byte[] bytes = new byte[entrada.available()];
                entrada.read(bytes, 0, entrada.available());
                bytesList.add(bytes);
            }
            bytesFinal = new byte[lengthFinal];
            int y = 0;
            for (byte[] byt : bytesList) {
                for (int i = 0; i < byt.length; i++) {
                    bytesFinal[y] = byt[i];
                    y++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return bytesFinal;
    }
    
}