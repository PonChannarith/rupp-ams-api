package org.rupp.amsruppapi.repository;

import org.apache.ibatis.annotations.*;
import org.rupp.amsruppapi.model.entity.AppUserProfile;

import java.util.List;

@Mapper
public interface AppUserProfileRepository {
    @Results({
            @Result(property = "appUserProfileId", column = "app_user_profile_id"),
            @Result(property = "firstName", column = "first_name"),
            @Result(property = "lastName", column = "last_name"),
            @Result(property = "dateOfBirth", column = "date_of_birth"),
            @Result(property = "placeOfBirth", column = "place_of_birth"),
            @Result(property = "currentAddress", column = "current_address"),
            @Result(property = "phoneNumber", column = "phone_number"),
            @Result(property = "cardId", column = "card_id"),
            @Result(property = "appUserId", column = "app_user_id"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    @Select("SELECT * FROM app_user_profile ORDER BY app_user_profile_id ASC")
    List<AppUserProfile> findAll();

    @Results({
            @Result(property = "appUserProfileId", column = "app_user_profile_id"),
            @Result(property = "firstName", column = "first_name"),
            @Result(property = "lastName", column = "last_name"),
            @Result(property = "dateOfBirth", column = "date_of_birth"),
            @Result(property = "placeOfBirth", column = "place_of_birth"),
            @Result(property = "currentAddress", column = "current_address"),
            @Result(property = "phoneNumber", column = "phone_number"),
            @Result(property = "cardId", column = "card_id"),
            @Result(property = "appUserId", column = "app_user_id"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    @Select("SELECT * FROM app_user_profile WHERE app_user_profile_id = #{id}")
    AppUserProfile findById(@Param("id") Long id);

    @Results({
            @Result(property = "appUserProfileId", column = "app_user_profile_id"),
            @Result(property = "firstName", column = "first_name"),
            @Result(property = "lastName", column = "last_name"),
            @Result(property = "dateOfBirth", column = "date_of_birth"),
            @Result(property = "placeOfBirth", column = "place_of_birth"),
            @Result(property = "currentAddress", column = "current_address"),
            @Result(property = "phoneNumber", column = "phone_number"),
            @Result(property = "cardId", column = "card_id"),
            @Result(property = "appUserId", column = "app_user_id")
    })
    @Select("SELECT * FROM app_user_profile WHERE app_user_id = #{appUserId}")
    AppUserProfile findByAppUserId(@Param("appUserId") Long appUserId);

    @Insert("INSERT INTO app_user_profile (first_name, last_name, date_of_birth, place_of_birth, current_address, phone_number, gender, card_id, nationality, app_user_id) " +
            "VALUES (#{firstName}, #{lastName}, #{dateOfBirth}, #{placeOfBirth}, #{currentAddress}, #{phoneNumber}, #{gender}, #{cardId}, #{nationality}, #{appUserId})")
    @Options(useGeneratedKeys = true, keyProperty = "appUserProfileId")
    int insert(AppUserProfile appUserProfile);

    @Update("UPDATE app_user_profile SET " +
            "first_name = #{firstName}, " +
            "last_name = #{lastName}, " +
            "date_of_birth = #{dateOfBirth}, " +
            "place_of_birth = #{placeOfBirth}, " +
            "current_address = #{currentAddress}, " +
            "phone_number = #{phoneNumber}, " +
            "gender = #{gender}, " +
            "card_id = #{cardId}, " +
            "nationality = #{nationality}, " +
            "updated_at = CURRENT_TIMESTAMP " +
            "WHERE app_user_profile_id = #{appUserProfileId}")
    int update(AppUserProfile appUserProfile);

    @Delete("DELETE FROM app_user_profile WHERE app_user_profile_id = #{id}")
    int deleteById(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM app_user_profile WHERE card_id = #{cardId} AND app_user_profile_id != #{excludeId}")
    int countByCardIdExcludingId(@Param("cardId") String cardId, @Param("excludeId") Long excludeId);

    @Select("SELECT COUNT(*) FROM app_user_profile WHERE card_id = #{cardId}")
    int countByCardId(@Param("cardId") String cardId);
}
