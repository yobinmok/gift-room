package com.example.capstone_giftcon

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.capstone_giftcon.dto.LocationData
import com.example.capstone_giftcon.dto.LocationResponse
import com.example.capstone_giftcon.network.RetrofitApi
import com.example.capstone_giftcon.network.ServiceApi
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapReverseGeoCoder
import net.daum.mf.map.api.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

// https://jangstory.tistory.com/41 : Kotlin
// https://mechacat.tistory.com/14
// https://webnautes.tistory.com/1319 : 안드로이드 인데 복붙 안됨 --> 채택!
class MapActivity : AppCompatActivity(), MapView.MapViewEventListener,
    SlidingUpPanelLayout.PanelSlideListener, MapView.CurrentLocationEventListener,
    MapView.POIItemEventListener,
    MapReverseGeoCoder.ReverseGeoCodingResultListener {

    private var service: ServiceApi = RetrofitApi.retrofitService
    private lateinit var mMapView: MapView
    private lateinit var longitude: String // 경도, y
    private lateinit var latitude: String // 위도, x
    private var keyword: String? = null
    private lateinit var layout: SlidingUpPanelLayout
    private lateinit var placeName: Array<String?>
    private lateinit var addressName: Array<String?>
    private lateinit var phone: Array<String?>
    private lateinit var x: Array<String?>
    private lateinit var y: Array<String?>
    private var oneTime = 0
    private var REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        val intent: Intent = intent
        keyword = intent.getStringExtra("keyword")
        getHashKey()
        mMapView = findViewById<View>(R.id.map_view) as MapView
        layout = findViewById(R.id.layout)

        mMapView.setCurrentLocationEventListener(this)
        mMapView.setPOIItemEventListener(this)
        mMapView.setMapViewEventListener(this) // 재원추가
        if (!checkLocationServiceStatus()) {
            showDialogForLocationServiceSetting()
        } else {
            checkRunTimePermission()
        }
    }

    private fun searchKeyword(data: LocationData) {
        service.getSearchKeyword(data).enqueue(object : Callback<List<LocationResponse>> {
            override fun onResponse(
                call: Call<List<LocationResponse>>,
                response: Response<List<LocationResponse>>
            ) {
                if (response.isSuccessful) {
                    val result: List<LocationResponse> = response.body()!!
                    val size = result.size // size관련 조건문 추가
                    placeName = arrayOfNulls(size)
                    addressName = arrayOfNulls(size)
                    phone = arrayOfNulls(size)
                    x = arrayOfNulls(size)
                    y = arrayOfNulls(size)
                    for (i in 0 until size) {
                        placeName[i] = result[i].place_name
                        addressName[i] = result[i].address_name
                        phone[i] = result[i].phone
                        x[i] = result[i].x
                        y[i] = result[i].y
                        setMarker(i, y[i], x[i], placeName[i])
                    }
                } else {
                    Log.d("searchKeyword", "response is not successful")
                    Log.e("searchKeyword", response.toString())
                }
            }

            override fun onFailure(call: Call<List<LocationResponse>>, t: Throwable) {
                Log.e("search 실패", t.message!!)
                t.printStackTrace()
            }
        })
    }

    private fun setMarker(tag: Int, latitude: String?, longitude: String?, placeName: String?) {
        mMapView.setMapCenterPoint(
            MapPoint.mapPointWithGeoCoord(
                latitude!!.toDouble(),
                longitude!!.toDouble()
            ), true
        )

        // 마커 생성 및 설정
        val marker = MapPOIItem()
        marker.itemName = placeName
        marker.tag = tag
        marker.mapPoint = MapPoint.mapPointWithGeoCoord(latitude.toDouble(), longitude.toDouble())
        marker.markerType = MapPOIItem.MarkerType.BluePin // 기본으로 제공하는 BluePin 마커 모양.
        marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        mMapView.addPOIItem(marker)
        //num++;
    }

    override fun onCurrentLocationUpdate(
        mapView: MapView,
        currentLocation: MapPoint,
        accuracylnMeters: Float
    ) {
        val mapPointGeo: MapPoint.GeoCoordinate = currentLocation.getMapPointGeoCoord()
        latitude = mapPointGeo.latitude.toString()
        longitude = mapPointGeo.longitude.toString()
        while (oneTime < 1) { // 한번만 실행하기 위해
            val data = LocationData(keyword!!, longitude.toDouble(), latitude.toDouble())
            searchKeyword(data)
            oneTime++
        }
        Log.d("Latitude", latitude)
        Log.d("Longitude", longitude)
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView.currentLocationTrackingMode =
            MapView.CurrentLocationTrackingMode.TrackingModeOff
        //        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
        mMapView.setShowCurrentLocationMarker(false)
    }

    override fun onReverseGeoCoderFoundAddress(mapReverseGeoCoder: MapReverseGeoCoder, s: String) {
        mapReverseGeoCoder.toString()
        onFinishReverseGeoCoding(s)
    }

    private fun onFinishReverseGeoCoding(result: String) {
        Log.d("Reverse Geo-coding: ", result) // 원래는 토스트 메시지
    }

    override fun onReverseGeoCoderFailedToFindAddress(mapReverseGeoCoder: MapReverseGeoCoder) {
        onFinishReverseGeoCoding("Fail")
    }

    override fun onCurrentLocationDeviceHeadingUpdate(mapView: MapView, v: Float) {
        //
    }

    override fun onCurrentLocationUpdateFailed(mapView: MapView) {
        //
    }

    override fun onCurrentLocationUpdateCancelled(mapView: MapView) {
        //
    }

    private fun checkRunTimePermission() {
        val hasFineLocationPermission: Int = ContextCompat.checkSelfPermission(
            this@MapActivity,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {
            mMapView.currentLocationTrackingMode =
                MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MapActivity,
                    REQUIRED_PERMISSIONS[0]
                )
            ) {
                ActivityCompat.requestPermissions(
                    this@MapActivity,
                    REQUIRED_PERMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                )
            } else {
                ActivityCompat.requestPermissions(
                    this@MapActivity,
                    REQUIRED_PERMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                )
            }
        }
    }

    private fun showDialogForLocationServiceSetting() {
        val builder = AlertDialog.Builder(this@MapActivity)
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage(
            """
    앱을 사용하기 위해서는 위치 서비스가 필요합니다.
    위치 설정을 수정하실래요?
    """.trimIndent()
        )
        builder.setCancelable(true)
        builder.setPositiveButton("설정", DialogInterface.OnClickListener { _, id ->
            val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE)
        })
        builder.setNegativeButton("취소", DialogInterface.OnClickListener { dialog, _ ->
            dialog.cancel() })
        builder.create().show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GPS_ENABLE_REQUEST_CODE -> if (checkLocationServiceStatus()) {
                if (checkLocationServiceStatus()) {
                    Log.d("@@@", "onActivityResult : GPS 활성화 되어있음")
                    checkRunTimePermission()
                    return
                }
            }
        }
    }

    private fun checkLocationServiceStatus(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    // 마커 클릭 관련
    override fun onPOIItemSelected(mapView: MapView, mapPOIItem: MapPOIItem) {}
    override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView, mapPOIItem: MapPOIItem) {
        val getTag: Int = mapPOIItem.getTag()
        val mapPlace: TextView = findViewById(R.id.place_name)
        val mapAddress: TextView = findViewById(R.id.address_name)
        val mapPhone: TextView = findViewById(R.id.phone)
        mapAddress.text = addressName[getTag]
        mapPlace.text = placeName[getTag]
        mapPhone.text = phone[getTag]

        //layout.setPanelHeight(300);
        layout.panelState = SlidingUpPanelLayout.PanelState.EXPANDED //말풍선 클릭시 슬라이드 패널 expanded, 재원 추가
    }

    override fun onBackPressed() { // 뒤로가기 했을 때, 재원 추가
        if (layout.panelState == SlidingUpPanelLayout.PanelState.EXPANDED) {
            layout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        } else {
            super.onBackPressed()
        }
    }

    override fun onCalloutBalloonOfPOIItemTouched(
        mapView: MapView,
        mapPOIItem: MapPOIItem,
        calloutBalloonButtonType: MapPOIItem.CalloutBalloonButtonType
    ) {
    }

    override fun onDraggablePOIItemMoved(
        mapView: MapView,
        mapPOIItem: MapPOIItem,
        mapPoint: MapPoint
    ) {
    }

    override fun onPanelSlide(panel: View, slideOffset: Float) {}
    override fun onPanelStateChanged(
        panel: View,
        previousState: SlidingUpPanelLayout.PanelState,
        newState: SlidingUpPanelLayout.PanelState
    ) {
    }

    override fun onMapViewInitialized(mapView: MapView) {}
    override fun onMapViewCenterPointMoved(mapView: MapView, mapPoint: MapPoint) {}
    override fun onMapViewZoomLevelChanged(mapView: MapView, i: Int) {}
    override fun onMapViewSingleTapped(mapView: MapView, mapPoint: MapPoint) {
        if (layout.panelState == SlidingUpPanelLayout.PanelState.EXPANDED) {
            layout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        }
    }

    override fun onMapViewDoubleTapped(mapView: MapView, mapPoint: MapPoint) {}
    override fun onMapViewLongPressed(mapView: MapView, mapPoint: MapPoint) {}
    override fun onMapViewDragStarted(mapView: MapView, mapPoint: MapPoint) {}
    override fun onMapViewDragEnded(mapView: MapView, mapPoint: MapPoint) {}
    override fun onMapViewMoveFinished(mapView: MapView, mapPoint: MapPoint) {}
    private fun getHashKey(){
            var packageInfo: PackageInfo? = null
            try {
                packageInfo = packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_SIGNATURES
                )
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            if (packageInfo == null) Log.e("KeyHash", "KeyHash:null")
            else{
                for (signature in packageInfo.signatures) {
                    try {
                        val md = MessageDigest.getInstance("SHA")
                        md.update(signature.toByteArray())
                        Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT))
                    } catch (e: NoSuchAlgorithmException) {
                        Log.e("KeyHash", "Unable to get MessageDigest. signature=$signature", e)
                    }
                }
            }
        }

    companion object {
        private const val LOG_TAG = "MapActivity"
        private const val GPS_ENABLE_REQUEST_CODE = 2001
        private const val PERMISSIONS_REQUEST_CODE = 100
    }
}