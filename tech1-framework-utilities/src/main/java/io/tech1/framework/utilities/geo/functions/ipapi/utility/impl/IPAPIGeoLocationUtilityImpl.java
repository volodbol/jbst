package io.tech1.framework.utilities.geo.functions.ipapi.utility.impl;

import io.tech1.framework.domain.exceptions.geo.GeoLocationNotFoundException;
import io.tech1.framework.domain.geo.GeoLocation;
import io.tech1.framework.domain.http.requests.IPAddress;
import io.tech1.framework.utilities.geo.functions.ipapi.feign.IPAPIFeign;
import io.tech1.framework.utilities.geo.functions.ipapi.utility.IPAPIGeoLocationUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IPAPIGeoLocationUtilityImpl implements IPAPIGeoLocationUtility {

    private final IPAPIFeign ipapiFeign;

    @Override
    public GeoLocation getGeoLocation(IPAddress ipAddress) throws GeoLocationNotFoundException {
        try {
            var queryResponse = this.ipapiFeign.getIPAPIResponse(ipAddress.getValue());
            if (queryResponse.isSuccess()) {
                return GeoLocation.processed(
                        ipAddress,
                        queryResponse.getCountry(),
                        queryResponse.getCountryCode(),
                        queryResponse.getCity()
                );
            } else {
                throw new GeoLocationNotFoundException(queryResponse.getMessage());
            }
        } catch (RuntimeException throwable) {
            throw new GeoLocationNotFoundException(throwable.getMessage());
        }
    }
}
