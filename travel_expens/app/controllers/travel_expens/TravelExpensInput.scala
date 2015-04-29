package controllers.travel_expens

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import common.TeConstant
import common.CommonUtil
import common.TravelExpensUtil
import model.travel_expens._
import form._
import scala.collection.mutable.ListBuffer
import org.joda.time.DateTime
import org.joda.time.format._
import org.joda.time.DateTimeZone


object TravelExpensInput extends Controller {
  
  /** 明細行の初期行数取得**/
  var classSelectbox = 1
  for (idn <- ItemMasterData.getInitDetailNum()) {
      classSelectbox = idn.item_cd.toInt
  }
  
  /** 旅費区分の取得**/
  //-DBから取得-//
  val tripClassLabelList = ItemMasterData.getItemTripClass()
  val tripClassLabel = Map.newBuilder[String, String]
  tripClassLabelList.map { tcll =>
      tripClassLabel += tcll.item_num -> tcll.item_name
  }
      
  /** 片道往復の取得**/
  val forwardPieceLabelList = ItemMasterData.getItemCd("forward_piece")
  val forwardPieceLabel     = Map.newBuilder[String, String]
  forwardPieceLabelList.map { fpll =>
      forwardPieceLabel += fpll.item_num -> fpll.item_name
  }
  /** 交通手段の取得**/
  val transportLabelList = ItemMasterData.getItemCd("transportation")
  val transportLabel     = Map.newBuilder[String, String]
  transportLabelList.map { tpll =>
      transportLabel += tpll.item_num -> tpll.item_name

  }
  var labelList = List(tripClassLabel.result,forwardPieceLabel.result,transportLabel.result)
  
  /**ヘッダと明細のForm作成**/
  val HeadForm:Form[TravelExpensForm.HeadFormData]     = Form(mapping("applyDate" -> text,"fullName" -> text,"maxRowNum"->number)(TravelExpensForm.HeadFormData.apply)(TravelExpensForm.HeadFormData.unapply))
  val DetailForm:Form[TravelExpensForm.DetailFormData] = Form(mapping("selectRow" -> list(text),"useDay" -> list(text),"travelReason" -> list(text),"tripClass"->list(text),"departure"->list(text),"destination"->list(text),"forwardPiece"->list(text),"transportation"->list(text),"transportExpens"->list(text),"accommodation"->list(text),"perDiem"->list(text),"remarks"->list(text))(TravelExpensForm.DetailFormData.apply)(TravelExpensForm.DetailFormData.unapply))
  
  
  
  
  /**入力画面初期表示**/
  def input = Action {
      val headData  = HeadForm.fill(TravelExpensForm.HeadFormData(CommonUtil.getDateYmString(), TeConstant.FULL_NAME,classSelectbox))
      val detailData= DetailForm
      Ok(views.html.travel_expens.input(headData , labelList , detailData))
  }
  
  /**データ登録**/
  def insertData()  = Action {implicit request =>

      /**ヘッダ情報入力**/
      val teCd = CommonUtil.creatUniqueCd()
     
      var applyDate    = CommonUtil.oneLineSeqToString(request.body.asFormUrlEncoded.get("apply_date")) 
      var fullName     = CommonUtil.oneLineSeqToString(request.body.asFormUrlEncoded.get("full_name"))
      TravelExpensData.insertHeadData(teCd,applyDate,fullName,CommonUtil.getDateTimeString(),null)
      
      /**明細情報入力**/
      val rowNum       = CommonUtil.oneLineSeqToString(request.body.asFormUrlEncoded.get("row_num")).toInt
      val detailMap = scala.collection.mutable.Map[String, String]()

      for(i <- 0 until rowNum) {
          detailMap+="use_day"-> CommonUtil.oneLineSeqToString(request.body.asFormUrlEncoded.get("use_day").slice(i, i+1))
          detailMap+="travel_reason"-> CommonUtil.oneLineSeqToString(request.body.asFormUrlEncoded.get("travel_reason").slice(i, i+1))
          detailMap+="trip_class"-> CommonUtil.oneLineSeqToString(request.body.asFormUrlEncoded.get("trip_class").slice(i, i+1))
          detailMap+="departure"-> CommonUtil.oneLineSeqToString(request.body.asFormUrlEncoded.get("departure").slice(i, i+1))
          detailMap+="destination"-> CommonUtil.oneLineSeqToString(request.body.asFormUrlEncoded.get("destination").slice(i, i+1))
          detailMap+="forward_piece"-> CommonUtil.oneLineSeqToString(request.body.asFormUrlEncoded.get("forward_piece").slice(i, i+1))
          detailMap+="transportation"-> CommonUtil.oneLineSeqToString(request.body.asFormUrlEncoded.get("transportation").slice(i, i+1))
          detailMap+="transport_expens"-> CommonUtil.oneLineSeqToString(request.body.asFormUrlEncoded.get("transport_expens").slice(i, i+1))
          detailMap+="accommodation"-> CommonUtil.oneLineSeqToString(request.body.asFormUrlEncoded.get("accommodation").slice(i, i+1))
          detailMap+="per_diem"-> CommonUtil.oneLineSeqToString(request.body.asFormUrlEncoded.get("per_diem").slice(i, i+1))
          detailMap+="remarks"-> CommonUtil.oneLineSeqToString(request.body.asFormUrlEncoded.get("remarks").slice(i, i+1))
          
          TravelExpensData.insertDetailData(CommonUtil.creatUniqueCd(),teCd,i.toString(),detailMap,CommonUtil.getDateTimeString(),null);
      }

      Redirect(routes.TravelExpensInput.input())
  }
  
  /**末尾行追加**/
  def addEndRow() = Action { implicit request =>
      
    var resHeadForm    = HeadForm.bindFromRequest()
    var resDetailForm  = DetailForm.bindFromRequest()
    
    /*** ヘッダ start ***/
    var listNum        = CommonUtil.oneLineSeqToString(request.body.asFormUrlEncoded.get("row_num")).toInt
    listNum+=1
    var applyDate      = CommonUtil.oneLineSeqToString(request.body.asFormUrlEncoded.get("apply_date"))
    var fullName       = CommonUtil.oneLineSeqToString(request.body.asFormUrlEncoded.get("full_name"))
    val headData       = resHeadForm.fill(TravelExpensForm.HeadFormData(applyDate.toString(), fullName.toString(),listNum))
    /*** ヘッダ end ***/
    
    /**明細部**/
    //checkbox存在判定
    val formEncData      = request.body.asFormUrlEncoded.get
    val selectRow        = CommonUtil.CreateCheckboxList(formEncData.get("select_row"),formEncData.contains("select_row"),listNum).toList
    
    val useDay           = request.body.asFormUrlEncoded.get("use_day").toList
    val travelReason     = request.body.asFormUrlEncoded.get("travel_reason").toList
    //**selectbox start**//
    val tripClass        = request.body.asFormUrlEncoded.get("trip_class").toList
    val departure        = request.body.asFormUrlEncoded.get("departure").toList
    val destination      = request.body.asFormUrlEncoded.get("destination").toList
    //**selectbox end**//
    val forwardPiece     = request.body.asFormUrlEncoded.get("forward_piece").toList
    val transportation   = request.body.asFormUrlEncoded.get("transportation").toList
    val transportExpens  = request.body.asFormUrlEncoded.get("transport_expens").toList
    val accommodation    = request.body.asFormUrlEncoded.get("accommodation").toList
    val perDiem          = request.body.asFormUrlEncoded.get("per_diem").toList
    val remarks          = request.body.asFormUrlEncoded.get("remarks").toList
    
    val detailDeta       = resDetailForm.fill(TravelExpensForm.DetailFormData(selectRow,useDay,travelReason,tripClass,departure,destination,forwardPiece,transportation,transportExpens,accommodation,perDiem,remarks))
    
    Ok(views.html.travel_expens.input(headData  ,labelList,detailDeta))
  }
  
  /**特定行追加**/
  def addspecificRow = Action { implicit request =>
      
    var resHeadForm     = HeadForm.bindFromRequest()
    var resDetailForm   = DetailForm.bindFromRequest()

    /*** ヘッダ start ***/
    var listNum         = CommonUtil.oneLineSeqToString(request.body.asFormUrlEncoded.get("row_num")).toInt
    listNum+=1
    var applyDate       = CommonUtil.oneLineSeqToString(request.body.asFormUrlEncoded.get("apply_date"))
    var fullName        = CommonUtil.oneLineSeqToString(request.body.asFormUrlEncoded.get("full_name"))
    val headData        = resHeadForm.fill(TravelExpensForm.HeadFormData(applyDate.toString(), fullName.toString(),listNum))
    /*** ヘッダ end ***/
    
    /***明細***/
    //ボタン押下行特定
    val selectRowNum    =  CommonUtil.oneLineSeqToString(request.body.asFormUrlEncoded.get("row_num_sel")).toInt
    //checkbox存在判定
    val formEncData     = request.body.asFormUrlEncoded.get
    var selectRow       = CommonUtil.CreateCheckboxList(formEncData.get("select_row"),formEncData.contains("select_row"),listNum-1).toList
    selectRow           = CommonUtil.addSpecificList(selectRow,selectRowNum).toList
    /***利用日***/
    var useDay          = request.body.asFormUrlEncoded.get("use_day").toList
    useDay              = CommonUtil.addSpecificList(useDay,selectRowNum).toList
    /***旅行事由***/
    var travelReason    = request.body.asFormUrlEncoded.get("travel_reason").toList
    travelReason        = CommonUtil.addSpecificList(travelReason,selectRowNum).toList
    //selectbox
    var tripClass       = request.body.asFormUrlEncoded.get("trip_class").toList
    tripClass           = CommonUtil.addSpecificList(tripClass,selectRowNum).toList
    var departure       = request.body.asFormUrlEncoded.get("departure").toList
    departure           = CommonUtil.addSpecificList(departure,selectRowNum).toList
    var destination     = request.body.asFormUrlEncoded.get("destination").toList
    destination         = CommonUtil.addSpecificList(destination,selectRowNum).toList
    //selectbox
    var forwardPiece    = request.body.asFormUrlEncoded.get("forward_piece").toList
    forwardPiece        = CommonUtil.addSpecificList(forwardPiece,selectRowNum).toList
    var transportation  = request.body.asFormUrlEncoded.get("transportation").toList
    transportation      = CommonUtil.addSpecificList(transportation,selectRowNum).toList
    /*** 交通費 ***/
    var transportExpens = request.body.asFormUrlEncoded.get("transport_expens").toList
    transportExpens     = CommonUtil.addSpecificList(transportExpens,selectRowNum).toList
    /*** 宿泊費 ***/
    var accommodation   = request.body.asFormUrlEncoded.get("accommodation").toList
    accommodation       = CommonUtil.addSpecificList(accommodation,selectRowNum).toList
    /*** 日当 ***/
    var perDiem         = request.body.asFormUrlEncoded.get("per_diem").toList
    perDiem             = CommonUtil.addSpecificList(perDiem,selectRowNum).toList
    /*** 備考 ***/
    var remarks         = request.body.asFormUrlEncoded.get("remarks").toList
    remarks             = CommonUtil.addSpecificList(remarks,selectRowNum).toList
    
    val detailDeta      = resDetailForm.fill(TravelExpensForm.DetailFormData(selectRow,useDay,travelReason,tripClass,departure,destination,forwardPiece,transportation,transportExpens,accommodation,perDiem,remarks))
    /***明細***/
    
    Ok(views.html.travel_expens.input(headData  ,labelList,detailDeta))
  }
  
  /**選択行追加**/
  def addSelectRow = Action { implicit request =>
      
    //checkbox存在判定
    val formEncData    = request.body.asFormUrlEncoded.get
    //checkbox選択数
    val selectNum      = CommonUtil.getCountCheckbox(formEncData.get("select_row"))
    val resHeadForm    = HeadForm.bindFromRequest()
    val resDetailForm  = DetailForm.bindFromRequest()
    
    /***ヘッダ start***/
    var listNum        = CommonUtil.oneLineSeqToString(request.body.asFormUrlEncoded.get("row_num")).toInt
    listNum+=selectNum
    val applyDate      = CommonUtil.oneLineSeqToString(request.body.asFormUrlEncoded.get("apply_date"))
    val fullName       = CommonUtil.oneLineSeqToString(request.body.asFormUrlEncoded.get("full_name"))
    val headData       = resHeadForm.fill(TravelExpensForm.HeadFormData(applyDate.toString(), fullName.toString(),listNum))
    /***ヘッダ end ***/
    
    /*** 明細 start ***/
    var selectRow      = CommonUtil.CreateCheckboxList(formEncData.get("select_row"),formEncData.contains("select_row"),listNum-1).toList
    selectRow          = CommonUtil.addSelectList(selectRow,request.body.asFormUrlEncoded.get("select_row")).toList
    
    /*** 利用日 ***/
    var useDay         = request.body.asFormUrlEncoded.get("use_day").toList
    useDay             = CommonUtil.addSelectList(useDay,request.body.asFormUrlEncoded.get("select_row")).toList

    var travelReason   = request.body.asFormUrlEncoded.get("travel_reason").toList
    travelReason       = CommonUtil.addSelectList(travelReason,request.body.asFormUrlEncoded.get("select_row")).toList
    //selectbox
    var tripClass      = request.body.asFormUrlEncoded.get("trip_class").toList
    tripClass          = CommonUtil.addSelectList(tripClass,request.body.asFormUrlEncoded.get("select_row")).toList
    var departure      = request.body.asFormUrlEncoded.get("departure").toList
    departure          = CommonUtil.addSelectList(departure,request.body.asFormUrlEncoded.get("select_row")).toList
    var destination    = request.body.asFormUrlEncoded.get("destination").toList
    destination          = CommonUtil.addSelectList(destination,request.body.asFormUrlEncoded.get("select_row")).toList
    //selectbox
    var forwardPiece   = request.body.asFormUrlEncoded.get("forward_piece").toList
    forwardPiece       = CommonUtil.addSelectList(forwardPiece,request.body.asFormUrlEncoded.get("select_row")).toList
    var transportation = request.body.asFormUrlEncoded.get("transportation").toList
    transportation     = CommonUtil.addSelectList(transportation,request.body.asFormUrlEncoded.get("select_row")).toList
    //
    var transportExpens= request.body.asFormUrlEncoded.get("transport_expens").toList
    transportExpens    = CommonUtil.addSelectList(transportExpens,request.body.asFormUrlEncoded.get("select_row")).toList
    var accommodation  = request.body.asFormUrlEncoded.get("accommodation").toList
    accommodation      = CommonUtil.addSelectList(accommodation,request.body.asFormUrlEncoded.get("select_row")).toList
    /*** 日当 ***/
    var perDiem        = request.body.asFormUrlEncoded.get("per_diem").toList
    perDiem            = CommonUtil.addSelectList(perDiem,request.body.asFormUrlEncoded.get("select_row")).toList
    /*** 備考 ***/
    var remarks        = request.body.asFormUrlEncoded.get("remarks").toList
    remarks            = CommonUtil.addSelectList(remarks,request.body.asFormUrlEncoded.get("select_row")).toList
    /*** 明細 end ***/
    
    val detailDeta     = resDetailForm.fill(TravelExpensForm.DetailFormData(selectRow,useDay,travelReason,tripClass,departure,destination,forwardPiece,transportation,transportExpens,accommodation,perDiem,remarks))
    
    Ok(views.html.travel_expens.input(headData  ,labelList,detailDeta))
  }
  
  /**コピー行追加**/
  def addSelectCopyRow = Action { implicit request =>
      
      //checkbox存在判定
      val formEncData    = request.body.asFormUrlEncoded.get
      //checkbox選択数
      val selectNum      = CommonUtil.getCountCheckbox(formEncData.get("select_row"))
      val resHeadForm    = HeadForm.bindFromRequest()
      val resDetailForm  = DetailForm.bindFromRequest()
      
      /***ヘッダ start***/
      var listNum        = CommonUtil.oneLineSeqToString(request.body.asFormUrlEncoded.get("row_num")).toInt
      listNum+=selectNum
      val applyDate      = CommonUtil.oneLineSeqToString(request.body.asFormUrlEncoded.get("apply_date"))
      val fullName       = CommonUtil.oneLineSeqToString(request.body.asFormUrlEncoded.get("full_name"))
      val headData       = resHeadForm.fill(TravelExpensForm.HeadFormData(applyDate.toString(), fullName.toString(),listNum))
      /***ヘッダ end***/
      
      /*** 明細 start ***/
      var selectRow      = CommonUtil.CreateCheckboxList(formEncData.get("select_row"),formEncData.contains("select_row"),listNum-1).toList
      selectRow          = CommonUtil.addSelectList(selectRow,request.body.asFormUrlEncoded.get("select_row")).toList
      /***利用日***/
      var useDay         = request.body.asFormUrlEncoded.get("use_day").toList
      useDay             = CommonUtil.addSelectCopyList(useDay,request.body.asFormUrlEncoded.get("select_row")).toList

      var travelReason   = request.body.asFormUrlEncoded.get("travel_reason").toList
      travelReason       = CommonUtil.addSelectCopyList(travelReason,request.body.asFormUrlEncoded.get("select_row")).toList
      //selectbox
      var tripClass      = request.body.asFormUrlEncoded.get("trip_class").toList
      tripClass          = CommonUtil.addSelectCopyList(tripClass,request.body.asFormUrlEncoded.get("select_row")).toList
      var departure      = request.body.asFormUrlEncoded.get("departure").toList
      departure          = CommonUtil.addSelectCopyList(departure,request.body.asFormUrlEncoded.get("select_row")).toList
      var destination    = request.body.asFormUrlEncoded.get("destination").toList
      destination        = CommonUtil.addSelectCopyList(destination,request.body.asFormUrlEncoded.get("select_row")).toList
      //selectbox
      var forwardPiece   = request.body.asFormUrlEncoded.get("forward_piece").toList
      forwardPiece       = CommonUtil.addSelectCopyList(forwardPiece,request.body.asFormUrlEncoded.get("select_row")).toList
      var transportation = request.body.asFormUrlEncoded.get("transportation").toList
      transportation     = CommonUtil.addSelectCopyList(transportation,request.body.asFormUrlEncoded.get("select_row")).toList

      var transportExpens= request.body.asFormUrlEncoded.get("transport_expens").toList
      transportExpens    = CommonUtil.addSelectCopyList(transportExpens,request.body.asFormUrlEncoded.get("select_row")).toList
      var accommodation  = request.body.asFormUrlEncoded.get("accommodation").toList
      accommodation      = CommonUtil.addSelectCopyList(accommodation,request.body.asFormUrlEncoded.get("select_row")).toList
      var perDiem        = request.body.asFormUrlEncoded.get("per_diem").toList
      perDiem            = CommonUtil.addSelectCopyList(perDiem,request.body.asFormUrlEncoded.get("select_row")).toList
      var remarks        = request.body.asFormUrlEncoded.get("remarks").toList
      remarks            = CommonUtil.addSelectCopyList(remarks,request.body.asFormUrlEncoded.get("select_row")).toList
      /*** 明細 end ***/
      
      val detailDeta    = resDetailForm.fill(TravelExpensForm.DetailFormData(selectRow,useDay,travelReason,tripClass,departure,destination,forwardPiece,transportation,transportExpens,accommodation,perDiem,remarks))
    
      Ok(views.html.travel_expens.input(headData  ,labelList,detailDeta))
  }
  
  /**画面未検出表示**/
  def notFoundPage(url: String) = Action { implicit request =>
      Ok(views.html.travel_expens.error.not_found())
  }
  
  /**非同期用**/
  def javascriptRoutes = Action { implicit request =>
      import routes.javascript._
      Ok(Routes.javascriptRouter("jsRoutes")(routes.javascript.TravelExpensInput.insertData,
              routes.javascript.TravelExpensInput.getStationData)).as("text/javascript")
  }
  
  /**未作成**/
  def getStationData() = Action { implicit request =>
      TravelExpensData.getHeadData()
      println(TravelExpensData.getHeadData())
      Ok(TravelExpensData.getHeadData().toString())
  }
}
