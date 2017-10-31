package com.codebroker.core.manager;

import com.codebroker.core.service.BaseCoreService;
import com.codebroker.util.PropertiesWrapper;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.Country;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;

/**
 * IP地理服务
 *
 * @author xl
 */
public class GeoIPService extends BaseCoreService {
    private FileInputStream database = null;
    private FileInputStream cityDatabase = null;
    private DatabaseReader countryReader = null;
    private DatabaseReader cityReader = null;

    @Override
    public void init(Object obj) {
        PropertiesWrapper configPropertieWrapper = (PropertiesWrapper) obj;
        try {
            String item = configPropertieWrapper.getProperty("GeoIP2_Country_File");
            database = new FileInputStream(item);
            countryReader = new DatabaseReader.Builder(database).build();

            String city = configPropertieWrapper.getProperty("GeoIP2_City_File");
            cityDatabase = new FileInputStream(city);
            cityReader = new DatabaseReader.Builder(cityDatabase).build();
        } catch (Exception e) {

        } finally {
            if (database != null) {
                try {
                    database.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (cityDatabase != null) {
                try {
                    cityDatabase.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        super.setActive();
    }

    /**
     * 获取city库的城市地理信息
     *
     * @param ip
     * @return
     */
    public Country getCityCountry(String ip) {
        try {
            // "128.101.101.101"
            InetAddress ipAddress = InetAddress.getByName(ip);
            CityResponse response = cityReader.city(ipAddress);
            Country country = response.getCountry();
            return country;
        } catch (IOException | GeoIp2Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据Ip获得国家库的地理信息
     *
     * @param ip
     * @return
     */
    public Country getCountry(String ip) {
        try {
            InetAddress ipAddress = InetAddress.getByName(ip);
            CountryResponse response = countryReader.country(ipAddress);

            Country country = response.getCountry();
            return country;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void destroy(Object obj) {
        if (countryReader != null) {
            try {
                countryReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (database != null) {
            try {
                database.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getName() {
        return getClass().getName();
    }

}
