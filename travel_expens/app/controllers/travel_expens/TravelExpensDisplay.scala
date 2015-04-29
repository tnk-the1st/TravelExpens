package controllers.travel_expens

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import anorm._
import form._
import model.travel_expens._
import common.CommonUtil


object TravelExpensDisplay extends Controller{
  
    /**ヘッダと明細のForm作成**/
    val HeadForm:Form[TravelExpensForm.HeadFormData]     = Form(mapping("applyDate" -> text,"fullName" -> text,"maxRowNum"->number)(TravelExpensForm.HeadFormData.apply)(TravelExpensForm.HeadFormData.unapply))
    val DetailForm:Form[TravelExpensForm.DetailFormData] = Form(mapping("selectRow" -> list(text),"useDay" -> list(text),"travelReason" -> list(text),"tripClass"->list(text),"departure"->list(text),"destination"->list(text),"forwardPiece"->list(text),"transportation"->list(text),"transportExpens"->list(text),"accommodation"->list(text),"perDiem"->list(text),"remarks"->list(text))(TravelExpensForm.DetailFormData.apply)(TravelExpensForm.DetailFormData.unapply)) 
      
    
    /**入力画面初期表示**/
    def displayAll = Action {
        var tempListBuf  =  new collection.mutable.ListBuffer[Map[String,String]]
        var monthListBuf =  new collection.mutable.ListBuffer[String]
        for(ted<-TravelExpensData.getHeadData()){
          val mapTemp = scala.collection.mutable.Map[String, String]()
          mapTemp += "te_cd"-> ted.te_cd
          mapTemp += "full_name" -> ted.full_name
          mapTemp += "apply_date" -> ted.apply_date
          mapTemp += "create_date" -> ted.create_date.toString()
          var mapCon = Map[String,String]()
          mapCon = mapTemp.toMap
          tempListBuf += mapCon
       }
       var tempList= tempListBuf.toList
      
      
       for(hd <- tempList){
           if(!hd.getOrElse("apply_date","").isEmpty())
               monthListBuf += hd.getOrElse("apply_date","")
       }
       var monthArray:Array[String] = null
       monthArray = monthListBuf.distinct.sorted.toArray
      
       tempList.zipWithIndex.map { case (item, index) => index + "番目の" + item }
       Ok(views.html.travel_expens.display_all(tempList,monthArray))
    }
    
    
    def confirm(te_cd_hidd:String)= Action {
      
      var applyDate=""
      var fullName =""
      TravelExpensData.getHeadDataSelect(te_cd_hidd).map { ted =>
          applyDate = ted.apply_date
          fullName = ted.full_name
      }
      val confHeadData  = HeadForm.fill(TravelExpensForm.HeadFormData(applyDate, fullName,TravelExpensData.conDetailRowNum(te_cd_hidd).toInt))
      
      //実験的に本来と違う形を提供することとする
      var selectRow       = new collection.mutable.ListBuffer[String]
      var useDay          = new collection.mutable.ListBuffer[String]
      var travelReason    = new collection.mutable.ListBuffer[String]
      var tripClass       = new collection.mutable.ListBuffer[String]
      var departure       = new collection.mutable.ListBuffer[String]
      var destination     = new collection.mutable.ListBuffer[String]
      var forwardPiece    = new collection.mutable.ListBuffer[String]
      var accommodation   = new collection.mutable.ListBuffer[String]
      var transportation  = new collection.mutable.ListBuffer[String]
      var transportExpens = new collection.mutable.ListBuffer[String]
      var perDiem         = new collection.mutable.ListBuffer[String]
      var remarks         = new collection.mutable.ListBuffer[String]
      
      
      TravelExpensData.getDetailDataSelect(te_cd_hidd).map { ted =>
        selectRow       +=ted.row_num
        useDay          += ted.use_day
        travelReason    += ted.travel_reason
        
        if(!ted.trip_class.isEmpty()){
            tripClass += CommonUtil.codeToName(ted.trip_class, "trip_class")
        } else {
          tripClass +=""
        }
        accommodation+=ted.accommodation
        departure    += ted.departure
        destination  += ted.destination
        
        if(!ted.forward_piece.isEmpty()){
            forwardPiece += CommonUtil.codeToName(ted.forward_piece, "forward_piece")
        } else {
          forwardPiece +=""
        }
        
        if(!ted.transportation.isEmpty()){
            transportation += CommonUtil.codeToName(ted.transportation, "transportation")
        } else {
          transportation +=""
        }
        transportExpens += ted.transport_expens
        perDiem      += ted.per_diem
        remarks      += ted.remarks
        
      }
      
      val confDetailData  = DetailForm.fill(TravelExpensForm.DetailFormData(selectRow.toList,useDay.toList,travelReason.toList,tripClass.toList,departure.toList,destination.toList,forwardPiece.toList,transportation.toList,transportExpens.toList,accommodation.toList,perDiem.toList,remarks.toList))

      Ok(views.html.travel_expens.confirm(confHeadData,confDetailData))
    }

}