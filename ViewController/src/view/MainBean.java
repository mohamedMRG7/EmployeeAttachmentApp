package view;

import com.prosecution.ucm.utils.ucm.UCMDocument;
import com.prosecution.ucm.utils.ucm.UCMUtilities;

import java.io.IOException;
import java.io.Serializable;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.faces.component.UIComponent;

import javax.faces.context.FacesContext;

import javax.faces.event.ActionEvent;

import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;

import oracle.adf.controller.v2.lifecycle.Lifecycle;
import oracle.adf.controller.v2.lifecycle.PagePhaseEvent;
import oracle.adf.controller.v2.lifecycle.PagePhaseListener;
import oracle.adf.model.BindingContext;
import oracle.adf.model.RegionBinding;
import oracle.adf.model.RegionContext;
import oracle.adf.model.RegionController;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.input.RichInputFile;
import oracle.adf.view.rich.component.rich.input.RichInputText;
import oracle.adf.view.rich.component.rich.layout.RichPanelGroupLayout;
import oracle.adf.view.rich.component.rich.nav.RichCommandButton;
import oracle.adf.view.rich.component.rich.nav.RichLink;
import oracle.adf.view.rich.context.AdfFacesContext;
import oracle.adf.view.rich.event.ItemEvent;
import oracle.adf.view.rich.render.ClientEvent;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

import oracle.stellent.ridc.IdcClientException;

import org.apache.myfaces.trinidad.component.UIXIterator;
import org.apache.myfaces.trinidad.event.DisclosureEvent;
import org.apache.myfaces.trinidad.event.PollEvent;
import org.apache.myfaces.trinidad.model.UploadedFile;

public class MainBean implements Serializable,RegionController {
    
    private RichPanelGroupLayout panalGroup;
    private BigDecimal userRowKEy;
    private RichInputText testText;
    public List<String> details=new ArrayList<>();
    public List<UploadedFile> uploadFiles=new ArrayList<>();
    public List<UploadedFileInfo> attachmentList=new ArrayList<>();
    private UIXIterator iteratorComp;
   
    UCMUtilities utils;
    private final String UCM_UPLOAD_PATH="/Enterprise Libraries/SearchPortal/Images/SubImages";

    public void setUploadFiles(List<UploadedFile> uploadFiles) {
        this.uploadFiles = uploadFiles;
    }

    public List<UploadedFile> getUploadFiles() {
        return uploadFiles;
    }

    public MainBean() {
        try {
           utils = new UCMUtilities("idc://10.3.1.224:4444", "weblogic", "weblogic1");
            utils.login();
        } catch (Exception e) {
        }
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }

    public List<String> getDetails() {
        return details;
    }


    public void addComponent(UIComponent parentUIComponent, UIComponent childUIComponent) {
        parentUIComponent.getChildren().add(childUIComponent);
        addPartialTrigger(childUIComponent);
    }

    public void addPartialTrigger(UIComponent component) {
        AdfFacesContext.getCurrentInstance().addPartialTarget(component);
    }
    public RichLink createImageLinkComp(String path, String fileName,String actionLisnerMethodNAme,UIComponent parent) {
        RichLink richImage = new RichLink();
        Random r = new Random();
        richImage.setId("rit1" + r.nextInt());
       
        richImage.setText(fileName);
        
        richImage.setInlineStyle("margin:20px;");
        addComponent(parent, richImage);
        return richImage;
    }
    
    
  
    public void setPanalGroup(RichPanelGroupLayout panalGroup) {
        this.panalGroup = panalGroup;
    }

    public RichPanelGroupLayout getPanalGroup() {
        return panalGroup;
    }
    
    
    
  

   
    
    public void triggeredByJSMethod(ClientEvent clientEvent) {

       //   createImageLinkComp("path", "fileName", "actionLisnerMethodNAme", getPanalGroup());
       // addPartialTrigger(getPanalGroup());
        System.out.println("HEy");
      }

    public void setUserRowKEy(BigDecimal userRowKEy) {
        this.userRowKEy = userRowKEy;
    }

    public BigDecimal getUserRowKEy() {
        return userRowKEy;
    }

    public String toUserDetailsAndAttachments() {
        // Add event code here...

        try {
            attachmentList = retriveAttachemtList();
        } catch (Exception e) {}
        return "details";
    }
    
    public static ViewObject getViewObjectFromIterator(String iteratorID) {
        DCIteratorBinding binding = getIterator(iteratorID);
        ViewObject VO = binding.getViewObject();
           return VO;
       }
    public static DCIteratorBinding getIterator(String iteratorID) {
           DCBindingContainer dcContainer = (DCBindingContainer) BindingContext.getCurrent().getCurrentBindingsEntry();
           DCIteratorBinding iterator = (DCIteratorBinding) dcContainer.get(iteratorID);
           return iterator;
       }

    public void onFragmentLoad(PollEvent pollEvent) {
        // Add event code here...
        
    }

    public void setTestText(RichInputText testText) {
        this.testText = testText;
    }

    public RichInputText getTestText() {
        return testText;
    }

    public void onUploadLisner(ValueChangeEvent valueChangeEvent) {
        // Add event code here...
        
        System.out.println(">>>>uploadLisner");
        String s=getSessionVariableValue(SubmitUploadValidation.SubmitKey.toString());
        System.out.println(s);
        if(getSessionVariableValue(SubmitUploadValidation.SubmitKey.toString())==SubmitUploadValidation.Save.toString()){
            System.out.println(">>>>>Upload fire");
        List<String> alreadyUploaded=new ArrayList<>();
        if(valueChangeEvent.getOldValue() !=valueChangeEvent.getNewValue()){
        RichInputFile mFile = (RichInputFile) valueChangeEvent.getComponent();
        String attachmentName =  mFile.getLabel();
       System.out.println(">>>Index "+getInfoFromName(attachmentName).getId());
       UploadedFileInfo fileInfo=getInfoFromName(attachmentName);
        try{
      List<UploadedFile>  uploadFiles = (List<UploadedFile>) valueChangeEvent.getNewValue();
            for(UploadedFile file:uploadFiles){
                System.out.println(">>>>Size "+alreadyUploaded.size()+" "+file.getOpaqueData());
                if(!alreadyUploaded.contains(file.getFilename()+file.getLength())){
                System.out.println(">>>>"+file.getFilename());
                String did= utils.checkInSpecificFolder(UCM_UPLOAD_PATH, file.getFilename(), file.getFilename()+new Random().nextInt(), file.getInputStream());
                
                System.out.println(">>>DID"+did);
                insertAttachmentToDB(fileInfo.getId(), fileInfo.getUserID(), did);
                alreadyUploaded.add(file.getFilename()+file.getLength());
                }
            }
        }catch(Exception e){System.out.println(e.getMessage());}
        
        }
        setSessionVariable(SubmitUploadValidation.SubmitKey.toString(), null);
        }
    }
    
    public void insertAttachmentToDB(String attachID,String userID,String DID)
    {
            ViewObject fromIterator = getViewObjectFromIterator("UploadedAttachemnts2Iterator");
            Row createRow = fromIterator.createRow();
            
            System.out.println(">>>>>>userID "+userID);
           // createRow.setAttribute(0,new Random().nextInt());
            createRow.setAttribute(1,Integer.valueOf(attachID));
            createRow.setAttribute(2,Integer.valueOf(userID));
            createRow.setAttribute(3, DID);
            fromIterator.insertRow(createRow);
    }
    
    public UploadedFileInfo getInfoFromName(String attachName)
    {
        UploadedFileInfo info=new UploadedFileInfo();
        for(UploadedFileInfo listInfo:attachmentList)
        {
            if(listInfo.getAttachmentName().equals(attachName))
            {
                info=listInfo;
            break;
            }
        }
        return info;
    }

    public void setAttachmentList(List<UploadedFileInfo> attachmentList) {
        this.attachmentList = attachmentList;
    }

    public List<UploadedFileInfo> getAttachmentList() {
        return attachmentList;
    }

    @Override
    public boolean refreshRegion(RegionContext regionContext) {
        // TODO Implement this method
     /*   int refreshFlag = regionContext.getRefreshFlag();
               if(refreshFlag==RegionBinding.PREPARE_MODEL){
            ViewObject fromIterator = getViewObjectFromIterator("Users1Iterator");
            Row currentRow = fromIterator.getCurrentRow();
            System.out.println(">>"+currentRow.getAttribute(1));
            
            ViewObject attachmentsCat = getViewObjectFromIterator("AttachmentCatagories1Iterator");
            
            setWhereCluse("CATEGORY_ID",attachmentsCat, String.valueOf(currentRow.getAttribute(2)));
            for(Row r:attachmentsCat.getAllRowsInRange()){
                ViewObject attachments = getViewObjectFromIterator("Attachments1Iterator");
                setWhereCluse("ATTACH_ID", attachments,String.valueOf(r.getAttribute(2)));
                attachments.next();
                Row row = attachments.getCurrentRow();
                String attachName = (String) row.getAttribute(1);
                attachmentList.add(attachName);
                System.out.println(">>>>>FragmentLisner"+attachmentList.size());
               
                try{
                addPartialTrigger(getIteratorComp());
                }catch(Exception e){}
            }
            
            

        }
               regionContext.getRegionBinding().refresh(refreshFlag);
        */
        return true;
    }

    public void setWhereCluse(String identifier,ViewObject v, String value){
            String whereClause = identifier+" = :deptBind";
                            v.setWhereClause(whereClause);
                            v.defineNamedWhereClauseParam("deptBind", null, null);
                            v.setNamedWhereClauseParam("deptBind",value);
                            v.executeQuery();
        
        
        }
    
    public void setWhereCluseTowParam(String identifier1,String identifier2,ViewObject v, String value1,String value2){
            String whereClause = identifier1+" = :deptBind" +" And "+identifier2+"= :bind2 ";
                            v.setWhereClause(whereClause);
                            v.defineNamedWhereClauseParam("deptBind", null, null);
                            v.defineNamedWhereClauseParam("bind2", null, null);
                            v.setNamedWhereClauseParam("deptBind",value1);
                            v.setNamedWhereClauseParam("bind2",value2);
                            v.executeQuery();
        
        
        }
        
    @Override
    public boolean validateRegion(RegionContext regionContext) {
        // TODO Implement this method
        return false;
    }

    @Override
    public boolean isRegionViewable(RegionContext regionContext) {
        // TODO Implement this method
        return false;
    }

    @Override
    public String getName() {
        // TODO Implement this method
        return null;
    }

    public void tabLisner(ItemEvent itemEvent) {
        // Add event code here...
        
        System.out.println(">>>Tab");
    }

    public void setIteratorComp(UIXIterator iteratorComp) {
        this.iteratorComp = iteratorComp;
    }

    public UIXIterator getIteratorComp() {
        return iteratorComp;
    }

    public void tabeDiscloser(DisclosureEvent disclosureEvent) {
        // Add event code here...
       
    }
    
    
    
    public List<UploadedFileInfo> retriveAttachemtList() throws IdcClientException, IOException {
        
           
            List<UploadedFileInfo> s=new ArrayList<>();
            ViewObject fromIterator = getViewObjectFromIterator("Users1Iterator");
            Row currentRow = fromIterator.getCurrentRow();
            String userID=String.valueOf( currentRow.getAttribute(0));
            //String catID=String.valueOf(currentRow.getAttribute(2));
          
            System.out.println(">>>>>MEthod "+currentRow.getAttribute(1));
            ViewObject attachmentsCat = getViewObjectFromIterator("AttachmentCatagories1Iterator");
            
            setWhereCluse("CATEGORY_ID",attachmentsCat,String.valueOf(currentRow.getAttribute(2)));
            for(Row r:attachmentsCat.getAllRowsInRange()){
                ViewObject attachments = getViewObjectFromIterator("Attachments1Iterator");
                setWhereCluse("ATTACH_ID", attachments,String.valueOf(r.getAttribute(2)));
               // attachments.next();
                Row row = attachments.getCurrentRow();
                String attachName = (String) row.getAttribute(1);
                String id=String.valueOf(row.getAttribute(0));
                
                //UploadedAttachemnts2Iterator
                List<String> urls=new ArrayList<>();
                List<UCMDocument> docs=new ArrayList<>();
                ViewObject uploadedFiles = getViewObjectFromIterator("UploadedAttachemnts2Iterator");
                setWhereCluseTowParam("ATTACH_ID", "USER_ID", uploadedFiles, id, userID);
                while(uploadedFiles.hasNext())
                {
                Row next = uploadedFiles.next();
                String did=String.valueOf(next.getAttribute(3));
                String url=utils.getDocUrlByDid(did);
                UCMDocument docInfo = utils.getDocumentInfoById(did);
                docs.add(docInfo);
                System.out.println(url);
                urls.add(url);
                System.out.println(">>>>> URLS "+docInfo.getDocUrl()+"   "+docInfo.getTitle()+ "    "+docs.size());
                
                }
               
               s.add(new UploadedFileInfo(id,urls,attachName,userID,docs));
               
               System.out.println(">>>>>MEthod "+s.size()+"   "+s.get(0).getUrls().size());
                
            }
            
          
        
        return s;
        
        }

    public void setSessionVariable(String key,String value){
            ADFContext adfCtx = ADFContext.getCurrent();
                 Map appScope = adfCtx.getApplicationScope();
                 appScope.put(key, value);
        }
    
    public String getSessionVariableValue(String key)
    {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map appScope = adfCtx.getApplicationScope();
         String appScopeValue=  (String)appScope.get(key);
          
          return appScopeValue;
    }

    public void rollbackActionLisner(ActionEvent actionEvent) {
        // Add event code here...
        System.out.println(">>Rollback");
    }

    public void cancelHoverLisner(ClientEvent clientEvent) {
        // Add event code here...
        System.out.println("cancel");
        setSessionVariable(SubmitUploadValidation.SubmitKey.toString(), SubmitUploadValidation.Cancel.toString());
    }

    public void submitHoverLisner(ClientEvent clientEvent) {
        // Add event code here...
        
        System.out.println("submit");
        setSessionVariable(SubmitUploadValidation.SubmitKey.toString(), SubmitUploadValidation.Save.toString());
    }
    
    
    
    enum SubmitUploadValidation{
        SubmitKey,Cancel,Save
        }
}
