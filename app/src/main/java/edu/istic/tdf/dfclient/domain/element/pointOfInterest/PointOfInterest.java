package edu.istic.tdf.dfclient.domain.element.pointOfInterest;

import com.raizlabs.android.dbflow.annotation.Table;

import edu.istic.tdf.dfclient.database.TdfDatabase;
import edu.istic.tdf.dfclient.domain.element.Element;
import edu.istic.tdf.dfclient.domain.element.ElementType;
import lombok.Getter;
import lombok.Setter;

/**
 * represent water point, two triangles
 * Created by guerin on 21/04/16.
 */
@Table(database = TdfDatabase.class)
public class PointOfInterest extends Element implements IPointOfInterest
{
    /**
     *
     * @return
     */
    @Getter
    @Setter
    private boolean isExternal;

    @Override
    public ElementType getType() {
        return ElementType.POINT_OF_INTEREST;
    }
}
