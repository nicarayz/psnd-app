package kh.com.psnd.network.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import core.lib.network.model.BaseGson;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class SectorType_label_6 extends BaseGson {

    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("secTypeID")
    public int    sectorTypeId;

    @ColumnInfo(name = "type")
    @SerializedName("sector_type")
    public String sectorType;

    @Override
    public String toString() {
        return sectorType;
    }
}