package model.travel_expens
import anorm._ 
import play.api.db._
import play.api.Play.current
import anorm.SqlParser._
import java.util.{Date}
import java.sql.Connection

case class TravelExpensHeadData(te_cd:String,apply_date:String,full_name:String,create_date:Date)
case class TravelExpensDetailData(te_cd:String,row_num:String,use_day:String,travel_reason:String,trip_class:String,departure:String,destination:String,forward_piece:String,transportation:String,transport_expens:String,accommodation:String,per_diem:String,remarks:String,create_date:Date)

object TravelExpensData {
    val headDataStruct = {
        get[String]("te_cd") ~ 
        get[String]("apply_date") ~
        get[String]("full_name") ~
        get[Date]("create_date") map {
            case te_cd~apply_date~full_name~create_date => TravelExpensHeadData(te_cd,apply_date,full_name,create_date)
        }
    }
    val detailDataStruct = {
        get[String]("te_cd") ~ 
        get[String]("row_num") ~
        get[String]("use_day") ~
        get[String]("travel_reason") ~
        get[String]("trip_class") ~
        get[String]("departure") ~
        get[String]("destination") ~
        get[String]("forward_piece") ~
        get[String]("transportation") ~
        get[String]("transport_expens") ~
        get[String]("accommodation") ~
        get[String]("per_diem") ~
        get[String]("remarks") ~
        get[Date]("create_date") map {
            case te_cd~row_num~use_day~travel_reason~trip_class~departure~destination~forward_piece~transportation~transport_expens~accommodation~per_diem~remarks~create_date => TravelExpensDetailData(te_cd,row_num,use_day,travel_reason,trip_class,departure,destination,forward_piece,transportation,transport_expens,accommodation,per_diem,remarks,create_date)
        }
    }
 
    
    /** 旅費コードを取得**/
    def getHeadDataSelect(teCd: String): List[TravelExpensHeadData] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM travel_expens.travel_expens_head WHERE te_cd = {te_cd}").on("te_cd" -> teCd).as(headDataStruct *)
    }
    def getDetailDataSelect(teCd: String): List[TravelExpensDetailData] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM travel_expens.travel_expens_detail WHERE te_cd = {te_cd} ORDER BY row_num ASC").on("te_cd" -> teCd).as(detailDataStruct *)
    }
    
    /** ヘッダデータを取得**/
    val aaa:anorm.ResultSetParser[String]=null
    def test(): String= DB.withConnection { implicit c =>
        SQL("SELECT * FROM travel_expens.travel_expens_head").as(aaa)
    }
    
    /** 明細行数の取得**/
    def conDetailRowNum(teCd: String): Long = DB.withConnection { implicit s =>
      SQL("SELECT count(*) FROM travel_expens.travel_expens_detail WHERE te_cd = {te_cd}").on("te_cd" -> teCd).as(scalar[Long].single)
    }
    
    
    /** ヘッダデータを取得**/
    def getHeadData(): List[TravelExpensHeadData] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM travel_expens.travel_expens_head").as(headDataStruct *)
    }
    
    /** ヘッダ情報をDBに挿入
     *  te_cd 旅費コード
     *  apply_date 申請年月
     *  full_name 申請社名
     *  create_date 申請日
     *  update_date 更新日
     *  **/
    def insertHeadData(te_cd: String,apply_date: String,full_name: String,create_date: String,update_date: String) { DB.withConnection { implicit c =>
      SQL("INSERT INTO travel_expens_head (te_cd,apply_date,full_name,create_date,update_date) VALUES ({te_cd},{apply_date},{full_name},{create_date},{update_date})").on(
          'te_cd -> te_cd,
          'apply_date -> apply_date,
          'full_name -> full_name,
          'create_date -> create_date,
          'update_date -> update_date
          ).executeInsert()
      }
    }
    /** 明細情報をDBに挿入**/
    def insertDetailData(detail_cd: String,
        te_cd: String,
        row_num: String,
        detailMap:scala.collection.mutable.Map[String,String],create_date: String,update_date: String) {
      DB.withConnection { implicit c =>
      SQL("INSERT INTO travel_expens_detail (detail_cd,te_cd ,row_num ,use_day , travel_reason,trip_class ,departure ,destination ,forward_piece,transportation,transport_expens,accommodation,per_diem,remarks,create_date,update_date) VALUES "+
          "({detail_cd},{te_cd},{row_num},{use_day},{travel_reason},{trip_class},{departure},{destination},{forward_piece},{transportation},{transport_expens},{accommodation},{per_diem},{remarks},{create_date},{update_date})").on(
          'detail_cd->detail_cd ,'te_cd->te_cd,'row_num->row_num ,'use_day->detailMap.get("use_day") ,'travel_reason->detailMap.get("travel_reason"),'trip_class->detailMap.get("trip_class"),'departure->detailMap.get("departure") ,
          'destination->detailMap.get("destination"),'forward_piece->detailMap.get("forward_piece"),'transportation->detailMap.get("transportation") ,'transport_expens->detailMap.get("transport_expens") ,
          'accommodation->detailMap.get("accommodation"),'per_diem->detailMap.get("per_diem"),'remarks->detailMap.get("remarks"),'create_date->create_date,'update_date->update_date).executeInsert()
      }
    }

}