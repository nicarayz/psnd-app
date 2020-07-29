package kh.com.psnd.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import kh.com.psnd.network.model.OfficeName_label_5;

@Dao
public interface OfficeNameDao_label_5 {

    @Query("SELECT * FROM OfficeName_label_5")
    List<OfficeName_label_5> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAll_Rx(OfficeName_label_5... items);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(OfficeName_label_5... items);
}