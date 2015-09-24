/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.unbosque.swii;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.commons.pool2.BaseObjectPool;
import org.apache.commons.pool2.KeyedObjectPool;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.testng.annotations.Test;

/**
 *
 * @author Alejandro
 */
public class PoolTest {
    
    public static final String pwd="p_5t6TsJu8";
    public static final int Total=1000;

    @Test(expectedExceptions =org.postgresql.util.PSQLException.class,
        expectedExceptionsMessageRegExp = ".*too many connections.*"
    )
    public void soloDebeCrear5Conexiones() throws Exception{
        FabricaConexiones fc=new FabricaConexiones("aretico.com",5432,"software_2","grupo7_5",pwd);
        ObjectPool<Connection> pool=new GenericObjectPool<Connection>(fc);
        for (int i = 0; i < 6; i++) {
            pool.borrowObject();           
        }                
    }
    
    @Test
    public void aprendiendoAControlarLasConexiones() throws Exception{
        FabricaConexiones fc=new FabricaConexiones("aretico.com",5432,"software_2","grupo7_5",pwd);
        ObjectPool<Connection> pool=new GenericObjectPool<Connection>(fc);
        for (int i = 0; i < 6; i++) {
            Connection c=pool.borrowObject();
            pool.returnObject(c);
        }                
    }
    
    @Test
    public void quePasaCuandoSeCierraUnaConexionAntesDeRetornarla() throws Exception{
        FabricaConexiones fc=new FabricaConexiones("aretico.com",5432,"software_2","grupo7_5",pwd);
        ObjectPool<Connection> pool=new GenericObjectPool<Connection>(fc);
        for (int i = 0; i < 6; i++) {
            Connection c=pool.borrowObject();
            pool.returnObject(c);
        }      
    }
    
    @Test
    public void quePasaCuandoSeRetornaUnaconexionContransaccionIniciada() throws Exception{
        FabricaConexiones fc=new FabricaConexiones("aretico.com",5432,"software_2","grupo7_5",pwd);
        ObjectPool<Connection> pool=new GenericObjectPool<Connection>(fc);
        Connection c=pool.borrowObject();
        c.close();
        pool.returnObject(c);            
    }
    
    @Test(threadPoolSize = 5, invocationCount = 5)
    public void midaTiemposParaInsertar1000RegistrosConSingleton() throws ClassNotFoundException, SQLException, Exception{
       long tiempo = System.currentTimeMillis();
        String SQL;
        for(int x=0; x < 1000 ;x ++)
        {
            FabricaConexiones fc=new FabricaConexiones("aretico.com",5432,"software_2","grupo7_5",pwd);
            ObjectPool<Connection> pool=new GenericObjectPool<Connection>(fc);
            Connection c=pool.borrowObject();
            SQL="INSERT INTO grupo7.RegistroHilos( registro, hilo, fecha) VALUES (?, ?, now());";
            PreparedStatement parametros = c.prepareStatement(SQL);
            parametros.setInt(1, x);
            parametros.setInt(2,(int)Thread.currentThread().getId() );
            parametros.executeUpdate();
            pool.returnObject(c);
        }
        System.out.println("Tiempo de ejecucion "+(System.currentTimeMillis()-tiempo));
    }
    
    @Test(threadPoolSize = 5, invocationCount = 5)
    public void midaTiemposParaInsertar1000RegistrosConObjectPool() throws ClassNotFoundException, SQLException{
        long tiempo = System.currentTimeMillis();
        String SQL;
        for(int x=0; x < 1000 ;x ++)
        {
            Connection conec = SingletonConnection.getConnection();
            SQL="INSERT INTO grupo7.RegistroHilos( registro, hilo, fecha) VALUES (?, ?, now());";
            PreparedStatement parametros = conec.prepareStatement(SQL);
            parametros.setInt(1, x);
            parametros.setInt(2,(int)Thread.currentThread().getId() );
            parametros.executeUpdate();
        }
        System.out.println("Tiempo de ejecucion "+(System.currentTimeMillis()-tiempo));
    }
}
