package nb.LimduMieya.retrofit

import nb.LimduMieya.data.Dust
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface NetWorkInterface {
    @GET("getCtprvnRltmMesureDnsty") //시도별 실시간 측정정보 조회 요청주소
    suspend fun getDust(@QueryMap param: HashMap<String, String>): Dust
}