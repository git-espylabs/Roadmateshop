package com.roadmate.shop.api.service

import com.roadmate.app.api.response.AppVersionMaster
import com.roadmate.app.api.response.ProductDetailsMaster
import com.roadmate.app.api.response.VehicleFuelTypeMaster
import com.roadmate.shop.api.response.VehicleBrandMaster
import com.roadmate.shop.api.response.VehicleModelMaster
import com.roadmate.shop.api.response.VehicleTypeMaster
import com.roadmate.shop.api.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiServices {

    @POST("shoplogin")
    suspend fun shopLogin(@Body jsonRequest : RequestBody): Response<RoadmateApiResponse>

    @GET("shop_categories")
    suspend fun getShopTypes(): Response<MoreServicesMaster>

    @Multipart
    @POST("shopreg")
    suspend fun shopSignUp(@Part image: MultipartBody.Part,
                           @Part type: MultipartBody.Part,
                           @Part shopname: MultipartBody.Part,
                           @Part phnum: MultipartBody.Part,
                           @Part phnum2: MultipartBody.Part,
                           @Part desc: MultipartBody.Part,
                           @Part opentime: MultipartBody.Part,
                           @Part closetime: MultipartBody.Part,
                           @Part agrimentverification_status: MultipartBody.Part,
                           @Part address: MultipartBody.Part,
                           @Part pincode: MultipartBody.Part,
                           @Part latitude: MultipartBody.Part,
                           @Part logitude: MultipartBody.Part,
                           @Part trans_id: MultipartBody.Part): Response<RoadmateApiResponse>

    @POST("otpshop")
    suspend fun verifyOTP(@Body jsonRequest : RequestBody): Response<OtpMaster>

    @GET("bannershop")
    suspend fun getAppBanners(): Response<AppBannerMaster>

    @POST("packagerandomdetails")
    suspend fun getBookedPackageDetails(@Body jsonRequest : RequestBody): Response<BookedPackageDetailsMaster>

    @POST("shopoffer_list")
    suspend fun getShopOffers(@Body jsonRequest : RequestBody): Response<ShopOffersMaster>

    @GET("vetype")
    suspend fun getVehicleTypes(): Response<VehicleTypeMaster>

    @POST("brand")
    suspend fun getVehicleBrands(@Body jsonRequest : RequestBody): Response<VehicleBrandMaster>

    @POST("model")
    suspend fun getVehicleModels(@Body jsonRequest : RequestBody): Response<VehicleModelMaster>

    @POST("package_features_list")
    suspend fun getFetureListOfPackage(@Body jsonRequest : RequestBody): Response<PackageFeatureMaster>

    @GET("fuel_type")
    suspend fun getVehicleFuelTypes(): Response<VehicleFuelTypeMaster>

    @Multipart
    @POST("shop_offers")
    suspend fun insertShopServiceOffers(@Part shop_id: MultipartBody.Part,
                                        @Part shop_cat_id: MultipartBody.Part,
                                        @Part title: MultipartBody.Part,
                                        @Part small_desc: MultipartBody.Part,
                                        @Part normal_amount: MultipartBody.Part,
                                        @Part offer_amount: MultipartBody.Part,
                                        @Part vehicle_typeid: MultipartBody.Part,
                                        @Part brand_id: MultipartBody.Part,
                                        @Part model_id: MultipartBody.Part,
                                        @Part offer_type: MultipartBody.Part,
                                        @Part image: MultipartBody.Part,
                                        @Part offer_end_date: MultipartBody.Part,
                                        @Part fuel_type: MultipartBody.Part,
                                        @Part image_uploded_status: MultipartBody.Part): Response<RoadmateApiResponse>

    @Multipart
    @POST("product_offers")
    suspend fun insertShopProductOffers(@Part image: MultipartBody.Part,
                                        @Part shopid: MultipartBody.Part,
                                        @Part title: MultipartBody.Part,
                                        @Part normal: MultipartBody.Part,
                                        @Part discount: MultipartBody.Part,
                                        @Part enddate: MultipartBody.Part,
                                        @Part description: MultipartBody.Part): Response<RoadmateApiResponse>

    @POST("customer_booktimeslotslist")
    suspend fun getBookedCustomersTimeList(@Body jsonRequest : RequestBody): Response<BookedCustomersMaster>

    @POST("sob_timelist")
    suspend fun getShopTimeSlotsList(@Body jsonRequest : RequestBody): Response<ShopTimeSlotMaster>

    @POST("shoppackagelist")
    suspend fun getShopPackageList(@Body jsonRequest : RequestBody): Response<PackageMaster>

    @GET("packagebanner")
    suspend fun getPackageBanners(): Response<AppBannerMaster>

    @POST("packagefulllist")
    suspend fun getAllPackageList(@Body jsonRequest : RequestBody): Response<PackageMaster>

    @POST("addshoppackage")
    suspend fun addPackage(@Body jsonRequest : RequestBody): Response<RoadmateApiResponse>

    /*@POST("mystorelist_shopdata")
    suspend fun getStoreProducts(@Body jsonRequest : RequestBody): Response<MyStoreProductMaster>*/

    @POST("mystorelist")
    suspend fun getStoreProducts(@Body jsonRequest : RequestBody): Response<MyStoreProductMaster>

    @POST("mystorelist_shop_new")
    suspend fun getMyProducts(@Body jsonRequest : RequestBody): Response<MyStoreProductMaster>

    /*@POST("mystorelist_shop")
    suspend fun getMyProducts(@Body jsonRequest : RequestBody): Response<MyStoreProductMaster>*/

    @Multipart
    @POST("storelistinsert")
    suspend fun insertProductToStore(@Part userid: MultipartBody.Part,
                                     @Part pro_name: MultipartBody.Part,
                                     @Part price: MultipartBody.Part,
                                     @Part category: MultipartBody.Part,
                                     @Part description: MultipartBody.Part,
                                     @Part usertype: MultipartBody.Part,
                                     @Part image1: MultipartBody.Part,
                                     @Part image2: MultipartBody.Part,
                                     @Part image3: MultipartBody.Part): Response<RoadmateApiResponse>

    @POST("singlestorelist")
    suspend fun getProductDetails(@Body jsonRequest : RequestBody): Response<ProductDetailsMaster>

    @POST("storebanner_shop")
    suspend fun getStoreBanners(@Body jsonRequest : RequestBody): Response<AppBannerMaster>



    @POST("shopimage")
    suspend fun getShopImage(@Body jsonRequest : RequestBody): Response<ShopProfileMaster>

    @POST("shopcostatus")
    suspend fun updateSHopStatus(@Body jsonRequest : RequestBody): Response<RoadmateApiResponse>

    @POST("shopcatlist")
    suspend fun getAddedShopTypes(@Body jsonRequest : RequestBody): Response<MoreServicesMaster>

    @POST("shopreviews")
    suspend fun getShopReviews(@Body jsonRequest : RequestBody): Response<ReviewMaster>

    @POST("suggcompinsert")
    suspend fun insertFeedback(@Body jsonRequest : RequestBody): Response<RoadmateApiResponse>

    @POST("shopprvdcat")
    suspend fun insertNewShopCategories(@Body jsonRequest : RequestBody): Response<RoadmateApiResponse>

    @GET("tclist")
    suspend fun getShopTermsAndConditions(): Response<TermsConditionsMaster>

    @POST("addshoptimeslot")
    suspend fun insertTimeSlots(@Body jsonRequest : RequestBody): Response<RoadmateApiResponse>

    @POST("updatenddate")
    suspend fun updateOfferEndDate(@Body jsonRequest : RequestBody): Response<RoadmateApiResponse>

    @POST("shoplistnew")
    suspend fun getVehicleBrandsForService(@Body jsonRequest : RequestBody): Response<VehicleBrandModelMaster>

    @POST("vmb_service_list")
    suspend fun getShopServicesList(@Body jsonRequest : RequestBody): Response<ShopServiceMaster>

    @POST("shop_services")
    suspend fun insertServiceSHop(@Body jsonRequest : RequestBody): Response<RoadmateApiResponse>

    @POST("vehalltypeserviceadd")
    suspend fun insertServiceSHop2(@Body jsonRequest : RequestBody): Response<RoadmateApiResponse>

    @POST("shopackagexists")
    suspend fun shopackagexists(@Body jsonRequest : RequestBody): Response<RoadmateApiResponse>

    @POST("timeslot_delete")
    suspend fun deleteTimeSlot(@Body jsonRequest : RequestBody): Response<RoadmateApiResponse>

    @POST("booktypelist")
    suspend fun getBookingDetails(@Body jsonRequest : RequestBody): Response<BookingTypeMaster>

    @POST("bookstatusupdation")
    suspend fun workCompleteStatusUpdate(@Body jsonRequest : RequestBody): Response<RoadmateApiResponse>

    @POST("updatepaystatus")
    suspend fun paymentStatusUpdate(@Body jsonRequest : RequestBody): Response<RoadmateApiResponse>

    @POST("shopregpaymentupdate")
    suspend fun insertPaymentToServer(@Body jsonRequest : RequestBody): Response<RoadmateApiResponse>

    @POST("shopgcmupdate")
    suspend fun registerForFcm(@Body jsonRequest : RequestBody): Response<RoadmateApiResponse>

    @POST("shopoffer_models")
    suspend fun insertOfferVehicleVehicle(@Body jsonRequest : RequestBody): Response<RoadmateApiResponse>

    @POST("shopnotificationlist")
    suspend fun getUserNotifications(@Body jsonRequest : RequestBody): Response<NotificationMaster>

    @POST("shopprovidedatdelete")
    suspend fun deleteCategory(@Body jsonRequest : RequestBody): Response<RoadmateApiResponse>

    @POST("shopprovidepackagedelete")
    suspend fun deletePackage(@Body jsonRequest : RequestBody): Response<RoadmateApiResponse>

    @POST("productofferdetails")
    suspend fun getProductOfferDetails(@Body jsonRequest : RequestBody): Response<ProductOfferMaster>

    @Multipart
    @POST("editshop")
    suspend fun shopEdit(@Part image: MultipartBody.Part,
                           @Part opentime: MultipartBody.Part,
                           @Part closetime: MultipartBody.Part,
                           @Part shopname: MultipartBody.Part,
                           @Part address: MultipartBody.Part,
                           @Part phonenumber: MultipartBody.Part,
                           @Part pincode: MultipartBody.Part,
                           @Part description: MultipartBody.Part,
                           @Part lattitude: MultipartBody.Part,
                           @Part logitude: MultipartBody.Part,
                           @Part shopid: MultipartBody.Part,
                         @Part img_status: MultipartBody.Part): Response<RoadmateApiResponse>

    @POST("addshopbankdetails")
    suspend fun submitBankDetails(@Body jsonRequest : RequestBody): Response<RoadmateApiResponse>

    @POST("updatesalestatus")
    suspend fun setProductSoldout(@Body jsonRequest : RequestBody): Response<RoadmateApiResponse>

    @POST("updateproductoffersale")
    suspend fun setProductOfferSoldout(@Body jsonRequest : RequestBody): Response<RoadmateApiResponse>

    @POST("shop_providedpackagecount")
    suspend fun geShopPackageCount(@Body jsonRequest : RequestBody): Response<ShopPackageCountMatser>

    @POST("app_version")
    suspend fun getAppVersionFromServer(@Body jsonRequest : RequestBody): Response<AppVersionMaster>

    @POST("shopoffer_new")
    suspend fun insertServiceOfferNew(@Body jsonRequest : RequestBody): Response<RoadmateApiResponse>
}