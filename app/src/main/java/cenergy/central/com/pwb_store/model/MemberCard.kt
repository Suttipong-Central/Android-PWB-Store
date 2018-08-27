package cenergy.central.com.pwb_store.model

/**
 * Created by Anuphap Suwannamas on 27/8/2018 AD.
 * Email: Anupharpae@gmail.com
 */

data class MemberCard(var cardNo: String = "",
                      var cardType: String = "",
                      var parentMemberName: String = "",
                      var pointsBalance: Double = 0.0,
                      var expiringPeriod: String = "")