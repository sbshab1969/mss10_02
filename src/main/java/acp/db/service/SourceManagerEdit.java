package acp.db.service;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.db.DbConnect;
import acp.db.domain.SourceClass;
import acp.db.utils.DbUtils;
import acp.forms.dto.SourceDto;
import acp.utils.DialogUtils;

public class SourceManagerEdit {
  private static Logger logger = LoggerFactory.getLogger(SourceManagerEdit.class);

  private SessionFactory sessionFactory;

  public SourceManagerEdit() {
    sessionFactory = DbConnect.getSessionFactory();
  }

  public List<String[]> getSources() {
    String strQuery = "select msss_id, msss_name from mss_source order by msss_name";
    List<String[]> arrayString = DbUtils.getListString(strQuery);
    return arrayString;
  }
  
  private SourceDto createDto(SourceClass objClass) {
    SourceDto objDto = new SourceDto();
    // ----------------------------------
    objDto.setId(objClass.getId());    
    objDto.setName(objClass.getName());    
//    objDto.setOwner(objClass.getOwner());    
    // ----------------------------------
    return objDto;
  }
  
  private void fillClassByDto(SourceClass objClass, SourceDto objDto) {
    // ----------------------------------
    objClass.setName(objDto.getName());
    if (objClass.getId() == null) {
      objClass.setDateCreate(DbUtils.getSysdate());    // !!!
    }
    objClass.setDateModify(DbUtils.getSysdate());    // !!!
    objClass.setOwner(DbUtils.getUser());    // !!!
    // ----------------------------------
  }

  public SourceDto select(Long objId) {
    SourceDto objDto = null; 
    Session session = sessionFactory.openSession();
    Transaction tx = session.beginTransaction();
    try {
      // ----------------------------------------
      SourceClass objClass = session.get(SourceClass.class, objId);
      objDto= createDto(objClass);
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

  public Long insert(SourceDto objDto) {
    Long objId = null;
    Session session = sessionFactory.openSession();
    Transaction tx = session.beginTransaction();
    try {
      // ---------------------------------
      SourceClass objClass = new SourceClass();
      fillClassByDto(objClass, objDto);        
      objId = (Long) session.save(objClass);
      // ---------------------------------
      tx.commit();
    } catch (HibernateException e) {
      tx.rollback();
      DialogUtils.errorPrint(e,logger);
      objId = null;
    } finally {
      session.close();
    }  
    return objId;
  }

  public boolean update(SourceDto objDto) {
    boolean res = false;
    Session session = sessionFactory.openSession();
    Transaction tx = session.beginTransaction();
    try {
      // --------------------
      SourceClass objClass = session.get(SourceClass.class, objDto.getId());
      fillClassByDto(objClass, objDto);        
      session.update(objClass);
      // --------------------
      tx.commit();
      res = true;
    } catch (HibernateException e) {
      tx.rollback();
      DialogUtils.errorPrint(e,logger);
      res = false;
    } finally {
      session.close();
    }  
    return res;
  }

  public boolean delete(Long objId) {
    boolean res = false;
    Session session = sessionFactory.openSession();
    Transaction tx = session.beginTransaction();
    try {
      // ---------------------------------------------------
      session.delete(session.load(SourceClass.class, objId));
      // ---------------------------------------------------
      tx.commit();
      res = true;
    } catch (HibernateException e) {
      tx.rollback();
      DialogUtils.errorPrint(e,logger);
      res = false;
    } finally {
      session.close();
    }  
    return res;
  }

}
