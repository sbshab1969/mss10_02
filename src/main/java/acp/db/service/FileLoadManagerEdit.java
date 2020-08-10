package acp.db.service;

import java.util.ArrayList;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.db.DbConnect;
import acp.db.domain.FileLoadClass;
import acp.forms.dto.FileLoadDto;
import acp.utils.DialogUtils;

public class FileLoadManagerEdit {
  private static Logger logger = LoggerFactory.getLogger(FileLoadManagerEdit.class);

  private SessionFactory sessionFactory;

  public FileLoadManagerEdit() {
    sessionFactory = DbConnect.getSessionFactory();
  }

  private FileLoadDto createDto(FileLoadClass objClass) {
    FileLoadDto objDto = new FileLoadDto();
    // ----------------------------------
    objDto.setId(objClass.getId());    
    objDto.setName(objClass.getName());    
    objDto.setMd5(objClass.getMd5());    
    objDto.setDateCreate(objClass.getDateCreate());
    objDto.setDateWork(objClass.getDateWork());
    objDto.setOwner(objClass.getOwner());
    objDto.setConfigId(objClass.getConfigId());
    if (objClass.getConfig() != null) {
      objDto.setConfigName(objClass.getConfig().getName());
    }
    objDto.setRecAll(objClass.getRecAll());
    objDto.setRecErr(objClass.getRecErr());
    // ----------------------------------
    ArrayList<String> statList = new ArrayList<>();
    statList.add(String.valueOf(objClass.getRecAll()));
    statList.add(String.valueOf(objClass.getRecErr()));
    objDto.setStatList(statList);
    // ----------------------------------
    return objDto;
  }

  public FileLoadDto select(Long objId) {
    FileLoadDto objDto = null; 
    Session session = sessionFactory.openSession();
    Transaction tx = session.beginTransaction();
    try {
      // ----------------------------------------
      Query query = session.createQuery("from FileLoadClass fl left outer join fetch fl.config cfg where fl.id=:id");
      query.setLong("id", objId);
      logger.info("\nQuery string: " + query.getQueryString());
      // ----------------------------------------
      FileLoadClass objClass = (FileLoadClass) query.uniqueResult();
      objDto = createDto(objClass);
      // ----------------------------------------
      tx.commit();
    } catch (HibernateException e) {
      tx.rollback();
      DialogUtils.errorPrint(e,logger);
      objDto = null;
    } finally {
      session.close();
    }  
    return objDto;
  }

}
