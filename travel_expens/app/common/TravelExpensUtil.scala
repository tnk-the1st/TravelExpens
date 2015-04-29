package common

object TravelExpensUtil {
    def oneSeqToString(originList:Seq[String]): String={
        var originString =""
        for (ol <- originList) {
           originString = ol
        }
        originString
    }
}