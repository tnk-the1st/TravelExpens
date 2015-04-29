package model.travel_expens
import anorm._ 
import play.api.db._
import play.api.Play.current
import anorm.SqlParser._

case class ItemMasterData(item_num:String,item_div:String,item_cd:String,item_name:String)


object ItemMasterData {
    val itemMasterStruct = {
        get[String]("item_num") ~ 
        get[String]("item_div") ~
        get[String]("item_cd") ~
        get[String]("item_name") map {
            case item_num~item_div~item_cd~item_name => ItemMasterData(item_num, item_div,item_cd,item_name)
        }
    }
    /** 旅費区分取得**/
    def getItemTripClass(): List[ItemMasterData] = DB.withConnection { implicit c =>
            SQL("SELECT * FROM travel_expens.item_master WHERE item_div = \'trip_class\'").as(itemMasterStruct *)
    }
    /** 初期明細行1件取得**/
    def getInitDetailNum(): List[ItemMasterData] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM travel_expens.item_master WHERE item_div = \'detail_num\'").as(itemMasterStruct *)
    }
    /** 項目種別によってitemMasterStructオブジェクトを取得**/
    def getItemCd(itemDiv: String): List[ItemMasterData] = DB.withConnection { implicit c =>
        SQL("SELECT * FROM travel_expens.item_master WHERE item_div = {item_div}").on('item_div -> itemDiv).as(itemMasterStruct *)
    }
    
    /** 項目種別によってitemMasterStructオブジェクトを取得**/
    def getItemName(itemNum: String,itemDiv: String): String = DB.withConnection { implicit c =>
        SQL("SELECT item_name FROM travel_expens.item_master WHERE item_div = {item_div} AND item_num = {item_num}").on('item_div -> itemDiv,'item_num->itemNum).as(scalar[String].single)
    }
     
}