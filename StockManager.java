public class StockManager {
    private Tree<Pair<Float, String>, String> priceTree;
    private Tree<String, Stock> stockIDTree;

    // Define sentinels as constants
    public static final Long LONG_SENTINEL_MAX = Long.MAX_VALUE;
    public static final Long LONG_SENTINEL_MIN = Long.MIN_VALUE;
    public static final String STRING_SENTINEL_MAX = "\uFFFF"; //the value is the maximum ASCII (unicode) value
    public static final String STRING_SENTINEL_MIN = "\u0000"; //the value is the minimum ASCII (unicode) value
    public static final Float FLOAT_SENTINEL_MAX = Float.POSITIVE_INFINITY;
    public static final Float FLOAT_SENTINEL_MIN = Float.NEGATIVE_INFINITY;

    public StockManager() {
        this.priceTree = new Tree<Pair<Float, String>, String>();
        this.stockIDTree = new Tree<String, Stock>();
    }

    // 1. Initialize the system
    public void initStocks() {
        priceTree.init(
                new Pair<>(FLOAT_SENTINEL_MIN, STRING_SENTINEL_MIN),
                new Pair<>(FLOAT_SENTINEL_MAX, STRING_SENTINEL_MAX)
        );
        stockIDTree.init(STRING_SENTINEL_MIN, STRING_SENTINEL_MAX);
    }


    // 2. Add a new stock
    public void addStock(String stockId, long timestamp, Float price) {
        if(timestamp <= 0) { //ensures timestamp is positive
            throw new IllegalArgumentException();
        }
        if (stockIDTree.search(stockIDTree.root, stockId) != null) {  // make sure this stock is not already exist in the tree
            throw new IllegalArgumentException();
        } else if (price <= 0){
            throw new IllegalArgumentException();
        }
        else {
            //create new Node in stockIDTree, create new stock and make it the node value
            // update the inner tree of the new stock and update current price
            // add the new node to the inner tree of the specific stock - happens in the constructor of stock automatically
            Stock newStock = new Stock(stockId, price, timestamp);
            Node<String, Stock> nodeOuterTree = new Node<String, Stock>(stockId, newStock);
            stockIDTree.insert(nodeOuterTree);
            // create new node in the priceTree - make sure it holds the current price as key and stockID as value
            Pair<Float,String> pair = new Pair<>(price, stockId);
            Node<Pair<Float,String>, String> node = new Node<>(pair, stockId);
            priceTree.insert(node);
        }
    }

    // 3. Remove a stock
    public void removeStock(String stockId) {
        // Call to search function to find the node with specific stockId we want to remove
        Node<String, Stock> deleteStock = stockIDTree.search(stockIDTree.root, stockId);
        if (deleteStock == null) {
            throw new IllegalArgumentException();
        }
        Float deleteStockCurrentPrice = deleteStock.value.getCurrentPrice();
        String deleteStockStockId = deleteStock.getKey();
        // Remove the Stock with the inner Tree updateStockEvents
        deleteStock.value = null;
        // Call to remove function to delete a specific stock - in stockId tree
        this.stockIDTree.delete(deleteStock);
        // After deleting the SockId the connected updateStockEvents tree will delete with the garbage collector
        // Call to search function to find the node with a specific price and StockId
        Pair<Float, String> priceKey = new Pair<>(deleteStockCurrentPrice, deleteStockStockId);
        Node<Pair<Float, String>, String> deletePrice = priceTree.search(priceTree.root, priceKey);
        // Call to remove function with Price tree and add check to remove only the Key with a specific Value - same as remove stock
        if (deletePrice != null) {
            priceTree.delete(deletePrice);
        }
    }

    // 4. Update a stock price
    public void updateStock(String stockId, long timestamp, Float priceDifference) {
        if(timestamp < 0) { //ensures timestamp is not negative
            throw new IllegalArgumentException();
        }
        if(priceDifference == 0) { // ensure priceDifference is not 0
            throw new IllegalArgumentException();
        }
        Node<String, Stock> nodeNeededToUpdate = stockIDTree.search(stockIDTree.root, stockId);
        if(nodeNeededToUpdate == null) {
            throw new IllegalArgumentException();
        }
        //  calculate the updates price
        float prevPrice = (nodeNeededToUpdate).value.currentPrice;
        float newPrice = prevPrice + priceDifference;
        nodeNeededToUpdate.value.currentPrice = newPrice; //  update the currentPrice property of the stock
        //  adding the priceDifference as a new node into the inner tree of the specific stock (using timestamp and priceDifference)
        Node<Long, Float> newUpdate = new Node<>(timestamp, priceDifference);
        nodeNeededToUpdate.value.updateStockEvents.insert(newUpdate);

        //  update the price of the stock in the priceTree by remove and insert it to the priceTree
        Pair<Float, String> complexKey = new Pair<>(prevPrice, stockId);
        Node<Pair<Float, String>, String> priceNodeToDelete = priceTree.search(priceTree.root, complexKey);

        priceTree.delete(priceNodeToDelete); // remove the node only from the priceTree
        Pair<Float, String> pairUpdated = new Pair<>(newPrice, stockId);
        Node<Pair<Float, String>, String> updatedNode = new Node<>(pairUpdated, stockId); // create the updated node we want to insert
        priceTree.insert(updatedNode); // insert the updates node to the priceTree
    }

    // 5. Get the current price of a stock
    public Float getStockPrice(String stockId) {
        // search the stockId in stockIDTree
        Node<String, Stock> node = stockIDTree.search(stockIDTree.root, stockId);
        if (node != null) { // the stock doesnt exist
            Float currentPrice = node.value.getCurrentPrice();
            return currentPrice; // return the current price
        } else {
            throw new IllegalArgumentException();
        }
    }

    // 6. Remove a specific timestamp from a stock's history
    public void removeStockTimestamp(String stockId, long timestamp) {
        // search the stockId in stockIDTree
        Node<String, Stock> StockId = stockIDTree.search(stockIDTree.root, stockId);
        if (StockId == null) { // the stock doesnt exist in the stockID tree
            throw new IllegalArgumentException();
        }
        //ensure the timestamp exists in the updateEventsTree of the specific Stock
        Node<Long, Float> timeStampEvent = StockId.value.updateStockEvents.search(StockId.value.updateStockEvents.root, timestamp);
        if (timeStampEvent == null) {
            throw new IllegalArgumentException();
        }

        // make sure the timestamp is not the one of the first adding event
        Node<String, Stock> node = stockIDTree.search(stockIDTree.root, stockId);
        if(timestamp == node.value.updateStockEvents.minimum().key) {
            throw new IllegalArgumentException();
        }

        Float prevPrice = StockId.value.getCurrentPrice();
        // if the stockId exist, then go to stockId.value.updateStockEvents there search for a specific date
        Node<Long, Float> Timestamp = StockId.value.updateStockEvents.search(StockId.value.updateStockEvents.root, timestamp);

        // price we need to remove
        Float price = Timestamp.value;
        StockId.value.currentPrice = StockId.value.currentPrice - price;

        // delete the node if found
        if (Timestamp == null) {
            throw new IllegalArgumentException();
        }
        StockId.value.updateStockEvents.delete(Timestamp);

        // Change price in proceTree
        Pair<Float, String> complexKey = new Pair<>(prevPrice, stockId);
        Node<Pair<Float, String>, String> priceNodeToDelete = priceTree.search(priceTree.root, complexKey);
        this.priceTree.delete(priceNodeToDelete);


        Pair<Float, String> complexKey2 = new Pair<>(StockId.value.currentPrice , stockId);
        Node<Pair<Float, String>, String> priceNodeToInsert= new Node<>(complexKey2, stockId);
        this.priceTree.insert(priceNodeToInsert);

    }

    // 7. Get the amount of stocks in a given price range
    public int getAmountStocksInPriceRange(Float price1, Float price2) {
        int rightBoundNum, leftBoundNum;
        if (price2 < price1) {
            throw new IllegalArgumentException();
        }

        //find the left bound
        // search if price1 exists in the tree
        Pair<Float, String> complexKey1 = new Pair<>(price1, null);
        Node<Pair<Float, String>, String> left = priceTree.search(priceTree.root, complexKey1);
        if(left == null) { //price 1 is not in the tree
            // add new node with the key of price1, then run successor on it in order to find the smallest node that is bigger than price1
            Node<Pair<Float, String>, String> nodeToAdd = new Node<>(complexKey1, null);
            priceTree.insert(nodeToAdd);
            Node<Pair<Float, String>, String> leftBound = priceTree.successor(nodeToAdd); //saving the right bound for the calculation
            if(leftBound != null) {
                leftBoundNum = priceTree.rank(leftBound) - 1;
                priceTree.delete(nodeToAdd);
            } else {
                return 0; //case that both price1 and price2 are bigger than all of the prices in the tree
            }

        } else { //price1 is in the tree
            leftBoundNum = priceTree.rank(left);
        }

        // find the right bound
        // search if price2 exists in the tree
        Pair<Float, String> complexKey2 = new Pair<>(price2, null);
        Node<Pair<Float, String>, String> right = priceTree.search(priceTree.root, complexKey2);
        if(right == null) { //price 2 is not in the tree
            // add new node with the key of price2, then use sister to find the node that is small but closer to price2
            Node<Pair<Float, String>, String> nodeToAdd2 = new Node<>(complexKey2, null);
            priceTree.insert(nodeToAdd2);
            nodeToAdd2.setSister();
            Node<Pair<Float, String>, String> rightBound = nodeToAdd2.sister; //saving the right bound for the calculation
            rightBoundNum = priceTree.rank(rightBound);
            priceTree.delete(nodeToAdd2);
        } else {
            Node<Pair<Float, String>, String> nodeToAdd2 = new Node<>(complexKey2, null);
            priceTree.insert(nodeToAdd2);
            Node<Pair<Float, String>, String> rightBound = nodeToAdd2.sister; //saving the right bound for the calculation
            rightBoundNum = priceTree.rank(rightBound);
            priceTree.delete(nodeToAdd2);
        }

        if(rightBoundNum == 1 && leftBoundNum == 1 && right == null) {
            return 0;
        }
        return (rightBoundNum - leftBoundNum + 1);
    }

    // 8. Get a list of stock IDs within a given price range
    public String[] getStocksInPriceRange(Float price1, Float price2) {
        if (price2 < price1) {
            throw new IllegalArgumentException();
        }

        int amountInPrice = getAmountStocksInPriceRange(price1, price2);
        if (amountInPrice == 0){
            String[] returnArr = new String[0];
            return returnArr;
        }

        String[] returnArr = new String[amountInPrice];

        // case 1 - priceTree contain price2
        Pair<Float, String> complexKey = new Pair<>(price2, null);
        Node<Pair<Float, String>, String> price2Node = priceTree.search(priceTree.root, complexKey);

        // case 2 - priceTree does not contain price2, then call successor to find the node with bigger value
        if (price2Node == null) { // the stock doesnt exist
            Node<Pair<Float, String>, String> tempNode = new Node<>(new Pair<>(price2, null), null); // create the updated node we want to insert
            priceTree.insert(tempNode);
            tempNode.setSister();
            Node<Pair<Float, String>, String> biggestInRange = tempNode.sister;
            priceTree.delete(tempNode);
            returnArr = getSistersArr(biggestInRange, price1, price2);
            return returnArr;
        }

        Node<Pair<Float, String>, String> tempNode = new Node<>(new Pair<>(price2, null), null); // create the updated node we want to insert
        priceTree.insert(tempNode);
        Node<Pair<Float, String>, String> biggerThenPrice2 = priceTree.successor(tempNode);
        priceTree.delete(tempNode);
        returnArr = getSistersArr(biggerThenPrice2, price1, price2);
        return returnArr;
    }

    public String[] getSistersArr(Node<Pair<Float, String>, String> node, Float price1, Float price2) {
        int amountInPrice = getAmountStocksInPriceRange(price1, price2);
        String[] returnArr = new String[amountInPrice];
        int i = amountInPrice - 1;

        // go to sister of node until you reach a node with a price less than price1
        while (node != null && i >= 0) {
            Float currentPrice = node.getKey().getKey(); // extract the Float part of the Pair
            if (currentPrice < price1) {
                break;
            }
            if (currentPrice <= price2) {
                returnArr[i] = node.getKey().getValue(); // extract the String part of the Pair
                i--;
            }
            node = node.sister; // move to the next sister node
        }

        return returnArr;
    }
}

