package com.codebroker.component.service;

import com.codebroker.component.BaseCoreService;
import com.codebroker.util.PropertiesWrapper;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.Country;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Optional;

/**
 * IP地理服务
 *
 * @author LongJu
 */
public class GeoIPComponent extends BaseCoreService {

    private FileInputStream database = null;
    private FileInputStream cityDatabase = null;
    private DatabaseReader countryReader = null;
    private DatabaseReader cityReader = null;

    @Override
    public void init(Object obj) {
        PropertiesWrapper propertiesWrapper = (PropertiesWrapper) obj;
        try {
            String item = propertiesWrapper.getProperty("GeoIP2_Country_File");
            database = new FileInputStream(item);
            countryReader = new DatabaseReader.Builder(database).build();

            String city = propertiesWrapper.getProperty("GeoIP2_City_File");
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
    public Optional<Country> getCityCountry(String ip) {
        try {
            // "128.101.101.101"
            InetAddress ipAddress = InetAddress.getByName(ip);
            CityResponse response = cityReader.city(ipAddress);
            Country country = response.getCountry();
            return Optional.of(country);
        } catch (IOException | GeoIp2Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * 根据Ip获得国家库的地理信息
     *
     * @param ip
     * @return
     */
    public Optional<Country> getCountry(String ip) {
        try {
            InetAddress ipAddress = InetAddress.getByName(ip);
            CountryResponse response = countryReader.country(ipAddress);

            Country country = response.getCountry();
            return Optional.of(country);
        } catch (Exception e) {
            return Optional.empty();
        }
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
