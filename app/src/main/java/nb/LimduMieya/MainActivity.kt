package nb.LimduMieya

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
//import com.android.miseya.data.DustItem
//import com.android.miseya.databinding.ActivityMainBinding
//import com.android.miseya.retrofit.NetWorkClient
import com.skydoves.powerspinner.IconSpinnerAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nb.LimduMieya.data.DustItem
import nb.LimduMieya.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    var items = mutableListOf<DustItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.spinnerViewSido.setOnSpinnerItemSelectedListener<String> { _, _, _, text ->
            communicateNetWork(setUpDustParameter(text))
        }

        binding.spinnerViewGoo.setOnSpinnerItemSelectedListener<String> { _, _, _, text ->

            Log.d("miseya", "selectedItem: spinnerViewGoo selected >  $text")
            var selectedItem = items.filter { f -> f.stationName == text }
            Log.d("miseya", "selectedItem: sidoName > " + selectedItem[0].sidoName)
            Log.d("miseya", "selectedItem: pm10Value > " + selectedItem[0].pm10Value)

            binding.tvCityname.text = selectedItem[0].sidoName + "  " + selectedItem[0].stationName
            binding.tvDate.text = selectedItem[0].dataTime
            binding.tvP10value.text = selectedItem[0].pm10Value + " ㎍/㎥"

            when (getGrade(selectedItem[0].pm10Value)) {
                1 -> {
                    binding.mainBg.setBackgroundColor(Color.parseColor("#9ED2EC"))
                    binding.ivFace.setImageResource(R.drawable.excellent)
                    binding.tvP10grade.text = "좋음"
                }

                2 -> {
                    binding.mainBg.setBackgroundColor(Color.parseColor("#D6A478"))
                    binding.ivFace.setImageResource(R.drawable.soso)
                    binding.tvP10grade.text = "보통"
                }

                3 -> {
                    binding.mainBg.setBackgroundColor(Color.parseColor("#DF7766"))
                    binding.ivFace.setImageResource(R.drawable.bad)
                    binding.tvP10grade.text = "나쁨"
                }

                4 -> {
                    binding.mainBg.setBackgroundColor(Color.parseColor("#3d221f"))
                    binding.ivFace.setImageResource(R.drawable.verybad)
                    binding.tvP10grade.text = "매우나쁨"
                }
            }
        }
    }

    private fun communicateNetWork(param: HashMap<String, String>) = lifecycleScope.launch() {
        val responseData = NetWorkClient.dustNetWork.getDust(param)
        Log.d("Parsing Dust ::", responseData.toString())

        val adapter = IconSpinnerAdapter(binding.spinnerViewGoo)
        items = responseData.response.dustBody.dustItem!!

        val goo = ArrayList<String>()
        items.forEach {
            Log.d("add Item :", it.stationName)
            goo.add(it.stationName)
        }

        runOnUiThread {
            binding.spinnerViewGoo.setItems(goo)
        }

    }

    private fun setUpDustParameter(sido: String): HashMap<String, String> {
        val authKey =
            "j+mqmbw45JRlKU7z27LrYJ09uhmEzL4OyxyvzCEGE5Cdub6L0K51C9mm5515wmEThkLz2d7NCaDDTKFSKJPiag=="

        return hashMapOf(
            "serviceKey" to authKey,
            "returnType" to "json",
            "numOfRows" to "100",
            "pageNo" to "1",
            "sidoName" to sido,
            "ver" to "1.0"
        )
    }

    //예외 처리랑,manifests 수정 23/12/13
    fun getGrade(value: String): Int {
        return try {
            val mValue = value.toInt()
            when {
                mValue >= 0 && mValue <= 30 -> 1
                mValue >= 31 && mValue <= 80 -> 2
                mValue >= 81 && mValue <= 100 -> 3
                else -> 4
            }
        } catch (e: NumberFormatException) {
            // 숫자로 변환할 수 없는 경우, 기본값 1을 반환하거나 예외처리에 맞게 처리
            1
        }
    }
}