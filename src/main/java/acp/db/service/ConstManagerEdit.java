package acp.db.service;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.db.DbConnect;
import acp.db.domain.ConstClass;
import acp.forms.dto.ConstDto;
import acp.utils.DialogUtils;

public class ConstManagerEdit {
  private static Logger logger = LoggerFactory.getLogger(ConstManagerEdit.class);

  private SessionFactory sessionFactory;

  public ConstManagerEdit() {
    sessionFactory = DbConnect.getSessionFactory();
  }

  private ConstDto createDto(ConstClass objClass) {
    ConstDto objDto = new ConstDto();
    // ----------------------------------
    objDto.setId(objClass.getId());    
    objDto.setName(objClass.getName());    
    objDto.setValue(objClass.getValue());    
    // ----------------------------------
    return objDto;
  }
  
  private void fillClassByDto(ConstClass objClass, ConstDto objDto) {
    // ----------------------------------
    objClass.setName(objDto.getName());    
    objClass.setValue(objDto.getValue());    
    // ----------------------------------
  }

  public ConstDto select(Long objId) {
    ConstDto objDto = null; 
    Session session = sessionFactory.openSession();
    Transaction tx = session.beginTransaction();
    try {
      // ----------------------------------------
      ConstClass objClass = session.get(ConstClass.class, objId);
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

  public Long insert(ConstDto objDto) {
    Long objId = null;
    Session session = sessionFactory.openSession();
    Transaction tx = session.beginTransaction();
    try {
      // ---------------------------
      ConstClass objClass = new ConstClass();
      fillClassByDto(objClass, objDto);        
      // Insert описан в XML -------------
      objId = (Long) session.save(objClass);
      // ---------------------------------
      tx.commit();
      // objId = objClass.getId();
    } catch (HibernateException e) {
      tx.rollback();
      DialogUtils.errorPrint(e,logger);
      objId = null;
    } finally {
      session.close();
    }  
    return objId;
  }
 
  public boolean update(ConstDto objDto) {
    boolean res = false;
    Session session = sessionFactory.openSession();
    Transaction tx = session.beginTransaction();
    try {
      // ---------------------------
      ConstClass objClass = session.get(ConstClass.class, objDto.getId());
      fillClassByDto(objClass, objDto);
      // --- Update описан в XML ---
      session.update(objClass);
      // ---------------------------
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
      session.delete(session.load(ConstClass.class, objId));
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
