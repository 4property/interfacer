package test;

import java.util.Arrays;
import java.util.List;

public class Sample16Class {
    /*
     * Merchant
     */
    record Merchant(String name, List<SoldItem> items) {
    }

    /*
     * SoldItem
     */
    record SoldItem(String name, double price, int month) {
    }

    // Local record pat
    record MonthlyMerchantSales(Merchant merchant, double sales) {
    }

    public String getName() {
        return "Sample16Class";
    }

    List<Merchant> findTopMerchants(List<Merchant> merchants, int month) {

        return merchants.stream()
                .map(merchant -> new MonthlyMerchantSales(merchant, computeSales(merchant.items(), month)))
                .sorted((m1, m2) -> Double.compare(m2.sales(), m1.sales()))
                .map(MonthlyMerchantSales::merchant)
                .collect(toList());
    }

    private double computeSales(List<SoldItem> items, int month) {
        return items.stream().filter(item -> item.month() == month).mapToDouble(SoldItem::price).sum();
    }

    public static void main(String[] args) {
        Sample14Class sample = new Sample14Class();
        List<Merchant> merchants = Arrays.asList(
                new Merchant("Alice", Arrays.asList(new SoldItem("item1", 100, 1), new SoldItem("item2", 200, 1))),
                new Merchant("Bob", Arrays.asList(new SoldItem("item1", 100, 1), new SoldItem("item2", 200, 1))));
        List<Merchant> topMerchants = sample.findTopMerchants(merchants, 1);
        System.out.println(topMerchants);
    }

}
