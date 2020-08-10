package acp.db.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.Date;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import acp.db.utils.*;
import acp.forms.dto.ConfigDto;
import acp.utils.*;

public class ConfigManagerList extends ManagerList {
  private static Logger logger = LoggerFactory.getLogger(ConfigManagerList.class);

  protected List<ConfigDto> cacheObj = new ArrayList<>();

  public ConfigManagerList() {
    headers = new String[] { 
        "ID"
      , Messages.getString("Column.Name")
      , Messages.getString("Column.SourceName")
      , Messages.getString("Column.DateBegin")
      , Messages.getString("Column.DateEnd")
      , Messages.getString("Column.Comment")
      , Messages.getString("Column.Owner") 
    };
    types = new Class<?>[] { 
        Long.class
      , String.class
      , String.class
      , Date.class
      , Date.class
      , String.class
      , String.class 
    };
    cntColumns = headers.length;

    fields = new String[] { 
        "cfg.id"
      , "cfg.name"
      , "cfg.dateBegin"
      , "cfg.dateEnd"
      , "cfg.comment"
      , "cfg.owner"
      , "src.name"
    };
    strFields = StrSqlUtils.buildSelectFields(fields, null);

    tableName = "ConfigClass";
    pkColumn = "cfg.id";
    strAwhere = null;
    seqId = 1000L;

    strFrom = "ConfigClass cfg left outer join cfg.source src";

    strWhere = strAwhere;
    strOrder = pkColumn;
    // ------------
    prepareQuery(null);
    // ------------
  }

  @Override
  public void prepareQuery(Map<String,String> mapFilter) {
    if (mapFilter != null) {
      setWhere(mapFilter);
    } else {
      strWhere = strAwhere;
    }
    strQuery = StrSqlUtils.buildQuery(strFields, strFrom, strWhere, strOrder);
    strQueryCnt = StrSqlUtils.buildQuery("select count(*)", strFrom, strWhere, null);
  }

  private void setWhere(Map<String,String> mapFilter) {
    // ----------------------------------
    String vName = mapFilter.get("name"); 
    String vOwner = mapFilter.get("owner"); 
    String vSource = mapFilter.get("source"); 
    // ----------------------------------
    String phWhere = null;
    String str = null;
    // ---
    if (!StrUtils.emptyString(vName)) {
      str = "upper(cfg.name) like upper('" + vName + "%')";
      phWhere = StrSqlUtils.strAddAnd(phWhere, str);
    }
    // ---
    if (!StrUtils.emptyString(vOwner)) {
      str = "upper(cfg.owner) like upper('" + vOwner + "%')";
      phWhere = StrSqlUtils.strAddAnd(phWhere, str);
    }
    // ---
    if (!StrUtils.emptyString(vSource)) {
      str = "cfg.sourceId=" + vSource;
      phWhere = StrSqlUtils.strAddAnd(phWhere, str);
    }
    // ---
    strWhere = StrSqlUtils.strAddAnd(strAwhere, phWhere);
  }
  
  @Override
  public long countRecords() {
    Long cnt = -1L;
    Session session = sessionFactory.openSession();
    Transaction tx = session.beginTransaction();
    try { 
      // -----------------------------------------------------------------
      Query query = session.createQuery(strQueryCnt);
      cnt = (Long) query.uniqueResult();
      // -----------------------------------------------------------------
      tx.commit();
    } catch (HibernateException e) {
      tx.rollback();
      DialogUtils.errorPrint(e,logger);
      cnt = -1L;
    } finally {
      session.close();
    }  
    return cnt;    
  }

  @Override
  public List<ConfigDto> queryAll() {
    cacheObj = fetchPage(-1,-1);
    return cacheObj;    
  }

  @Override
  public List<ConfigDto> fetchPage(int startPos, int cntRows) {
    Session session = sessionFactory.openSession();
    Transaction tx = session.beginTransaction();
    try { 
      // HQL -------------------------------------
      Query query = session.createQuery(strQuery);
      if (startPos>0) {
        query.setFirstResult(startPos-1);  // Hibernate начинает с 0
      }
      if (cntRows>0) {
        query.setMaxResults(cntRows);
      }  
      // ==============
      fillCache(query);
      // ==============
      tx.commit();
    } catch (HibernateException e) {
      tx.rollback();
      DialogUtils.errorPrint(e,logger);
    } finally {
      session.close();
    }  
    return cacheObj;    
  }  

  private void fillCache(Query query) {
    logger.info("\nQuery string: " + query.getQueryString());
    // ============================
    List<?> objList = query.list();
    // ============================
    cacheObj = new ArrayList<>();
    for (int i=0; i < objList.size(); i++) {
      Object[] obj = (Object[]) objList.get(i);
      cacheObj.add(getObject(obj));
    }
  }
  
  private ConfigDto getObject(Object[] obj) {
    //---------------------------------------
    Long rsId = (Long) obj[0];
    String rsName = (String) obj[1];
    Timestamp rsDateBegin = (Timestamp) obj[2];
    Timestamp rsDateEnd = (Timestamp) obj[3];
    String rsComment = (String) obj[4];
    String rsOwner = (String) obj[5];
    String rsSourceName = (String) obj[6];
    //---------------------------------------
    ConfigDto objDto = new ConfigDto();
    objDto.setId(rsId);
    objDto.setName(rsName);
    objDto.setDateBegin(rsDateBegin);
    objDto.setDateEnd(rsDateEnd);
    objDto.setComment(rsComment);
    objDto.setOwner(rsOwner);
    objDto.setSourceName(rsSourceName);
    //---------------------------------------
    return objDto;
  }

}
