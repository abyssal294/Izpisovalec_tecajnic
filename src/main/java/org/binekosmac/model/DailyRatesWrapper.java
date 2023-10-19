package org.binekosmac.model;

import javax.xml.bind.annotation.*;
import java.util.List;

// @XmlRootElement(name = "DtecBS", namespace = "http://www.bsi.si")
// @XmlAccessorType(XmlAccessType.FIELD)
public class DailyRatesWrapper {

    @XmlElement(name = "tecajnica")
    private List<DailyRates> dailyRatesList;

    public DailyRatesWrapper(List<DailyRates> dailyRatesList) {
        this.dailyRatesList = dailyRatesList;
    }

    public DailyRatesWrapper(){};

    public List<DailyRates> getDailyRatesList() {
        return dailyRatesList;
    }

    public void setDailyRatesList(List<DailyRates> dailyRatesList) {
        this.dailyRatesList = dailyRatesList;
    }

}
