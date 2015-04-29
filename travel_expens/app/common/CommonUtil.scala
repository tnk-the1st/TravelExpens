package common

import org.joda.time.DateTime
import org.joda.time.format._
import org.joda.time.DateTimeZone
import model.travel_expens._

object CommonUtil {
    
  /**
   * 1行のSeqを文字列に変換する
   * @param  Seq[String]: orgList   postデータ各入力項目情報
   * @return String:      orgString 引数項目データの文字列
   */
  def oneLineSeqToString(orgList:Seq[String]): String={
        var orgString =""
        for (ol <- orgList) {
           orgString = ol
        }
        orgString
  }
  /**
   * チェックボックスの選択数を取得する
   * @param  Option[Seq[String]]: optSeqSelectRow   postデータ各入力項目情報
   * @return Int:      orgString 引数項目データの文字列
   */
  def getCountCheckbox(optSelectRow: Option[Seq[String]]): Int={
        var countNum = 0
        for (seqSelectRow <- optSelectRow) {
            for(sr<-seqSelectRow){
                countNum+= 1
            }        
        }
        countNum
  }
  
  /**
   * 特定行追加時にListに空行を追加する（各行のボタン）
   * @param  List[String]:       rowList       指定行項目List
   * @param  Int:                selectRowNum  選択行数
   * @return ListBuffer[String]: rowListBuf    空行を追加したListBuffer
   */
  def addSpecificList(rowList:List[String],selectRowNum:Int): collection.mutable.ListBuffer[String]={
      var rowListBuf =  new collection.mutable.ListBuffer[String]
      var j = 1
      for (rowListString <- rowList) {          
          if(j == selectRowNum+1){
              rowListBuf +=""
          } 
          rowListBuf += rowListString
          j+=1
      }
      rowListBuf
  }
  /**
   * 選択行追加時にListに空行を追加する
   * @param  List[String]:       rowList       指定行項目List
   * @param  Seq[String]:        selectNumList  複数選択行
   * @return ListBuffer[String]: rowListBuf    空行を追加したListBuffer
   */
  def addSelectList(rowList:List[String],selectNumList:Seq[String]): collection.mutable.ListBuffer[String]={
      var rowListBuf =  new collection.mutable.ListBuffer[String]
      var i = 1
      for (rowListString <- rowList) {
          for(snl<-selectNumList){
              if(i == snl.toInt+1){
                  rowListBuf +=""
              } 
          }
          rowListBuf += rowListString
          i+=1
      }
      rowListBuf
  }
  /**
   * 選択行コピー行追加時にListに選択項目を追加する
   * @param  List[String]:       rowList       指定行項目List
   * @param  Seq[String]:        selectNumList  複数選択行
   * @return ListBuffer[String]: rowListBuf    空行を追加したListBuffer
   */
  def addSelectCopyList(rowList:List[String],selectNumList:Seq[String]): collection.mutable.ListBuffer[String]={
      var rowListBuf =  new collection.mutable.ListBuffer[String]
      var i = 1
      for (rowListString <- rowList) {
          for(snl<-selectNumList){
              if(i == snl.toInt){
                  rowListBuf +=rowListString
              }
          }
          rowListBuf += rowListString
          i+=1
      }
      rowListBuf
  }
  
  
  /**
   * チェックボックスListに空行を追加する
   * @param  List[String]:       rowList       指定行項目List
   * @param  Seq[String]:        selectNumList  複数選択行
   * @return ListBuffer[String]: rowListBuf    空行を追加したListBuffer
   */
  def CreateCheckboxList(selectRowSeq:Option[Seq[String]],formEncBool:Boolean,listNum:Int): collection.mutable.ListBuffer[String]={
      val selectRowListBuf = new collection.mutable.ListBuffer[String]
      var checkFlag =0
      if(formEncBool){
        for(i:Int <- 1 to listNum){
            for(selectRowSeq <- selectRowSeq){
                for(sr<-selectRowSeq){
                    if(i == sr.toInt){
                        selectRowListBuf += sr
                        checkFlag=1
                    }
                }
            }
            if(checkFlag!=1){
                selectRowListBuf +=""
            } else {
                checkFlag =0
            }
            
        }
    } else {
        for(i:Int <- 1 to listNum){
            selectRowListBuf +=""
        }
    }
    selectRowListBuf
  }
  /**
   * yyyy/MMの現在時刻を取得する
   *　@return String: applyDateString yyyy/MM型文字列
   */
  def getDateYmString(): String={
    val applyDateTime = new DateTime()
    var applyDateString = DateTimeFormat.forPattern("yyyy/MM").print(applyDateTime.withZone(DateTimeZone.UTC))
    applyDateString
  }
  
    /**
    * DateTime型の現在時刻を取得する
    * @return String: applyDateString　DateTime型文字列
    */
   def getDateTimeString(): String={
     val applyDateTime = new DateTime()
     var applyDateString = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").print(applyDateTime.withZone(DateTimeZone.UTC))
     applyDateString
   }
  

  /**
　　 * 10桁の乱数生成
 　　* @return String: randomCd 10桁乱数文字列
 　　*/
  def creatRandomCd10(): String={
    val randomCd = new scala.util.Random(new java.security.SecureRandom()).alphanumeric.take(10).mkString
    randomCd
  }
  /**
   * ユニークコードの生成
   * @see    String: creatRandomCd10 10桁乱数文字列
   * @see    String: getHeadData　旅費コード
   * @return String: teCd ユニークコード文字列
   */
  def creatUniqueCd(): String={

    var teCd = creatRandomCd10()
    val teCdDbAll = TravelExpensData.getHeadData()

    teCdDbAll.map { tcda =>
        if(teCd == tcda.te_cd){
          teCd =creatRandomCd10
        }
    }
    teCd
  }
  
  def codeToName(itemNum:String,itemDiv:String): String={
      ItemMasterData.getItemName(itemNum,itemDiv)
  }
}
