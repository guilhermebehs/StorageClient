
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
             for(int i = 0; i < buf.length; i++)
                 saida.write(buf[i]);
             sinalizarFimArquivo(saida);
             id = entrada.read();
             socketServidor.close();
         } catch (IOException ex) {
             ex.printStackTrace();
         }
        
         return id;
        
    }
    
    public void retornarArquivo(int id){
        
        List<Integer> ints = new ArrayList();
        
         try {
             socketServidor = new Socket("localhost", 8090);
             entrada = socketServidor.getInputStream();
             saida = socketServidor.getOutputStream();
             saida.write(Operacao.RETORNAR_BYTES.valor);
             saida.write(id);
             int b = 0;
             while((b = entrada.read()) != -1)
                   ints.add(b);
             byte[]  bytes = new byte[ints.size()];
           
              for(int i =0; i < ints.size(); i++)
                  bytes[i] = ints.get(i).byteValue();
               
             FileOutputStream out = new FileOutputStream("arquivoDownload.txt");
             out.write(bytes);
             out.close();
             
         } catch (IOException ex) {
            ex.printStackTrace();
         }
        
    }
    
    
     public void sinalizarFimArquivo(OutputStream saida){
         
        try {
            saida.write(-1);
            saida.write(-1);
            saida.write(-1);
            saida.write(-1);
            saida.write(-1);        
        } catch (IOException ex) {
            ex.printStackTrace();
        }
         
     }

}