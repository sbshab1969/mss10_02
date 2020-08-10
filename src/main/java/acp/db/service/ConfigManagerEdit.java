package acp.db.service;

import java.sql.Timestamp;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.db.DbConnect;
import acp.db.domain.ConfigClass;
import acp.db.utils.*;
import acp.forms.dto.ConfigDto;
import acp.utils.*;

public class ConfigManagerEdit {
  private static Logger logger = LoggerFactory.getLogger(ConfigManagerEdit.class);

  private SessionFactory sessionFactory;

  public ConfigManagerEdit() {
    sessionFactory = DbConnect.getSessionFactory();
  }

  private ConfigDto createDto(ConfigClass objClass) {
    ConfigDto objDto = new ConfigDto();
    // ----------------------------------
    objDto.setId(objClass.getId());    
    objDto.setName(objClass.getName());    
    // objDto.setConfig(objClass.getConfigR());  // R !!!!
    objDto.setDateBegin(objClass.getDateBegin());    
    objDto.setDateEnd(objClass.getDateEnd());    
    objDto.setComment(objClass.getComment());    
    // objDto.setOwner(objClass.getOwner());    
    objDto.setSourceId(objClass.getSourceId());    
    // objDto.setSourceName(null);    
    // ----------------------------------
    return objDto;
  }

  private void fillClassByDto(ConfigClass objClass, ConfigDto objDto) {
    String emptyXml = "<?xml version=\"1.0\"?><config><sverka.ats/></config>";
    Timestamp sysdt = DbUtils.getSysdate();
    String usr = DbUtils.getUser();
    // ----------------------------------
    objClass.setName(objDto.getName());    
    if (objClass.getId() == null) {
      objClass.setConfigW(emptyXml);
    } else {
      objClass.setConfigW(objClass.getConfigR());   // !!! R -> W
    }
    objClass.setDateBegin(objDto.getDateBegin());    
    objClass.setDateEnd(objDto.getDateEnd());    
    objClass.setComment(objDto.getComment());    
    if (objClass.getId() == null) {
      objClass.setDateCreate(sysdt);
    }
    objClass.setDateModify(sysdt);
    objClass.setOwner(usr);
    objClass.setSourceId(objDto.getSourceId());    
    // ----------------------------------
  }
  
  public ConfigDto select(Long objId) {
    ConfigDto objDto = null; 
    Session session = sessionFactory.openSession();
    Transaction tx = session.beginTransaction();
    try {
      // ----------------------------------------
      ConfigClass objClass = session.get(ConfigClass.class, objId);
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

  public String getCfgName(Long objId) {
    // ------------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("select name from ConfigClass where id=:id");
    String strQuery = sbQuery.toString();
    // ------------------------------------------------------
    String configName = "";
    Session session = sessionFactory.openSession();
    Transaction tx = session.beginTransaction();
    try {
      Query query = session.createQuery(strQuery);
      query.setLong("id",objId);
      logger.info("\nQuery string: " + query.getQueryString());
      // --------------------
      configName = (String) query.uniqueResult();
      // --------------------
      tx.commit();
    } catch (HibernateException e) {
      DialogUtils.errorPrint(e,logger);
      configName = "";
    }
    // ------------------------------------------------------
    return configName;
  }

  public String getCfgStr(Long objId) {
    // ------------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("select configR from ConfigClass where id=:id");
    String strQuery = sbQuery.toString();
    // ------------------------------------------------------
    String configStr = null;
    Session session = sessionFactory.openSession();
    Transaction tx = session.beginTransaction();
    try {
      Query query = session.createQuery(strQuery);
      query.setLong("id",objId);
      logger.info("\nQuery string: " + query.getQueryString());
      // --------------------
      configStr = (String) query.uniqueResult();
      // --------------------
      tx.commit();
    } catch (HibernateException e) {
      DialogUtils.errorPrint(e,logger);
      configStr = null;
    }
    // ------------------------------------------------------
    return configStr;
  }
 
  public Long insert(ConfigDto objDto) {
    Long objId = null;
    Session session = sessionFactory.openSession();
    Transaction tx = session.beginTransaction();
    try {
      // ---------------------------------
      ConfigClass objClass = new ConfigClass();
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

  public boolean update(ConfigDto objDto) {
    boolean res = false;
    Session session = sessionFactory.openSession();
    Transaction tx = session.beginTransaction();
    try {
      // --------------------
      ConfigClass objClass = session.get(ConfigClass.class, objDto.getId());
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

  public boolean updateCfgStr(Long objId, String txtConf) {
    boolean res = false;
    // -----------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("update mss_options");
    //sbQuery.append(" set msso_config=:conf"); // OK
    sbQuery.append("   set msso_config=XMLType(:conf)");  // OK
    sbQuery.append(", msso_dt_modify=sysdate");
    sbQuery.append(", msso_owner=user");
    sbQuery.append(" where msso_id=:id");
    String strQuery = sbQuery.toString();
    // -----------------------------------------
    Session session = sessionFactory.openSession();
    Transaction tx = session.beginTransaction();
    try {
      SQLQuery query = session.createSQLQuery(strQuery);
      query.setLong("id", objId);
      query.setString("conf", txtConf);
      logger.info("\nQuery string: " + query.getQueryString());
      // --------------------
      query.executeUpdate();
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
    // -----------------------------------------------------
    return res;
  }

  public boolean delete(Long objId) {
    boolean res = false;
    Session session = sessionFactory.openSession();
    Transaction tx = session.beginTransaction();
    try {
      // ---------------------------------------------------
      session.delete(session.load(ConfigClass.class, objId));
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
 
  public boolean copy(Long objId) {
    boolean res = false;
    // -----------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("insert into mss_options");
    sbQuery.append(" (select msso_seq.nextval, msso_name || '_copy'");
    sbQuery.append(", msso_config");
    sbQuery.append(", msso_dt_begin, msso_dt_end, msso_comment");
    sbQuery.append(", sysdate, sysdate, user, msso_msss_id");
    sbQuery.append(" from mss_options where msso_id=:id)");
    String strQuery = sbQuery.toString();
    // -----------------------------------------------------
    Session session = sessionFactory.openSession();
    Transaction tx = session.beginTransaction();
    try {
      SQLQuery query = session.createSQLQuery(strQuery);
      query.setLong("id", objId);
      logger.info("\nQuery string: " + query.getQueryString());
      // --------------------
      query.executeUpdate();
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
    // -----------------------------------------------------
    return res;
  }

}
