public class Stock {
    public String stockID;
    public Tree<Long, Float> updateStockEvents;
    public Float currentPrice;

    public static final Long LONG_SENTINEL_MAX = Long.MAX_VALUE;
    public static final Long LONG_SENTINEL_MIN = Long.MIN_VALUE;

    Stock(String stockID, Float currentPrice, long timestamp) {
        this.stockID = stockID;     // update the new stockID
        this.updateStockEvents = new Tree<>();  //create new tree of the inner updates of the specific stock
        this.updateStockEvents.init(LONG_SENTINEL_MIN, LONG_SENTINEL_MAX);  // init the updates tree
        this.currentPrice = currentPrice;
        Node<Long, Float> node = new Node<>(timestamp, currentPrice);
        this.updateStockEvents.insert(node);    //insert the first price into the updatesTree
    }

    public Float getCurrentPrice() {
        return this.currentPrice;
    }
}