package acp.forms.dm;

import java.util.ArrayList;
import java.util.List;

import acp.db.service.ConfigManagerList;
import acp.forms.dto.ConfigDto;

public class DmConfigList extends DmPanel {
  private static final long serialVersionUID = 1L;

  private ConfigManagerList tableManager;
  private List<ConfigDto> cacheObj = new ArrayList<>();

  public DmConfigList(ConfigManagerList tblMng) {
    tableManager = tblMng;
    setHeaders();
  }

  public void setManager(ConfigManagerList tblMng) {
    tableManager = tblMng;
    setHeaders();
  }
  
  private void setHeaders() {
    if (tableManager != null) {
      headers = tableManager.getHeaders();
      types = tableManager.getTypes();
      colCount = headers.length;
    } else {
      headers = new String[] {};
      types = new Class<?>[] {};
      colCount = 0;
    }
  }
  
  // --- TableModel ---

  @Override
  public int getRowCount() {
    return cacheObj.size();
  }

  @Override
  public Object getValueAt(int row, int col) {
    ConfigDto obj = cacheObj.get(row); 
    switch (col) {
    case 0:  
      return obj.getId();
    case 1:  
      return obj.getName();
    case 2:  
      return obj.getSourceName();
    case 3:
      return obj.getDateBegin();
    case 4:
      return obj.getDateEnd();
    case 5:
      return obj.getComment();
    case 6:
      return obj.getOwner();
    }  
    return null;
  }
  // --------------------------------------
  
  @Override
  public long countRecords() {
    long recCnt = tableManager.countRecords();
    return recCnt;
  }

  @Override
  public void queryAll() {
    cacheObj = tableManager.queryAll();
    fireTableChanged(null);
  }

  @Override
  public void queryPage() {
    calcPageCount();
    if (currPage > pageCount) {
      currPage = pageCount;
    }  
    tableManager.openQueryPage();
    fetchPage(currPage);
  }

  @Override
  public void fetchPage(long page) {
    long startRec = calcStartRec(page);
    if (testStartRec(startRec) == false) { 
      return;
    }
    setCurrPage(page);
    int startPos = (int) startRec;
    cacheObj = tableManager.fetchPage(startPos,recPerPage);
    fireTableChanged(null);
  }

  @Override
  public void closeQuery() {
    tableManager.closeQuery();
  }

}
