import java.util.ArrayList;

public class TxHandler {

    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */

    UTXO ut;
    ArrayList<UTXO> ut1;
    ArrayList<Transaction.Output> txunspend;
    UTXOPool upool;
    public TxHandler(UTXOPool utxoPool) {
        // IMPLEMENT THIS
        System.out.println("Starting Function TxHandler");
        upool = new UTXOPool(utxoPool);
        ut1 = upool.getAllUTXO();
        int i=0;
        txunspend = new ArrayList<Transaction.Output>();
        for (UTXO a:ut1
             ) {
            txunspend.add(utxoPool.getTxOutput(a));
        }

        //System.out.println(ut1);
        UTXOPool utxoPool1 = new UTXOPool(utxoPool);

    }

    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool, 
     * (2) the signatures on each input of {@code tx} are valid, 
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {
        // IMPLEMENT THIS
        System.out.println("Starting function isvalidtx");
        int flag = 1;
        int ii = 0;
        double sumInput = 0;
        double sumOutput = 0;
        ArrayList<Transaction.Input> input1 = tx.getInputs();
        ArrayList<UTXO> utxoused;
        ArrayList <Transaction.Output> out1 = tx.getOutputs();
        int []outindex = new int[200];
        //verify signature
        System.out.println("NumInputs : " + tx.numInputs());
        System.out.println("NumOutputs : " + tx.numOutputs());

        //int numprevtx = 0;
        int iter1 = 0;

        for (Transaction.Input a:input1
             ) {
            sumOutput = 0;
            iter1++;
            int iter2 = 0;

            //System.out.println("Transaction Inputs : " + a.outputIndex);
            UTXO spentoutpututxo = new UTXO(a.prevTxHash,a.outputIndex);
            if (!upool.contains(spentoutpututxo)){
                System.out.println("Not in the utxopool. Double Spending");
                return false;
            }

            if (spentoutpututxo != null){
                //System.out.println("spentoutpututxo" + spentoutpututxo);
                Transaction.Output spentoutput = upool.getTxOutput(spentoutpututxo);
                //getting input sum


                if (spentoutput != null){
                    sumInput = sumInput + spentoutput.value;
                    boolean versig = Crypto.verifySignature(spentoutput.address, tx.getRawDataToSign(ii++), a.signature);
                    //output is negative

                    if (!versig) {
                        System.out.println("In the false condition");
                        return false;
                    }else{
                        for (Transaction.Output ou: out1
                                ) {
                            if (ou.value < 0)
                            {
                                System.out.println("ou value is negative");
                                return false;
                            }else{
                                sumOutput = sumOutput + ou.value;
                            }
                        }

                    }
//                    System.out.println("removing utxo from pool");
//                    upool.removeUTXO(spentoutpututxo);
                }else {
                    System.out.println("Spentoutput is null");
                    return false;
                }
            }else
            {
                System.out.println("spentoutpututxo is null");
                //return false;
            }



        }

        /***************************************/

        if (sumInput < sumOutput)
        {
            System.out.println("sumInput: " + sumInput);
            System.out.println("sumOutput : " + sumOutput );
            return false;
        }
        //    return false;

        /****************************************/
        System.out.println("Returning True");
//        for (Transaction.Input inp:input1
//             ) {
//
//                    UTXO spentoutpututxo = new UTXO(inp.prevTxHash,inp.outputIndex);
//                    //System.out.println("removing utxo from pool");
//                    upool.removeUTXO(spentoutpututxo);
//               }
        return true;
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        // IMPLEMENT THIS
        System.out.println("Starting function handletx");
        Transaction[] trns = new Transaction[upool.getAllUTXO().size()];
        int i=0;

        for (Transaction a:possibleTxs
             ) {
            ArrayList<Transaction.Input> input1 = a.getInputs();
            int prevsize = upool.getAllUTXO().size();
            if (isValidTx(a)){
                for (Transaction.Input inp:input1
                        ) {

                    UTXO spentoutpututxo = new UTXO(inp.prevTxHash,inp.outputIndex);
                    //System.out.println("removing utxo from pool");
                    upool.removeUTXO(spentoutpututxo);
                }

                int currsize = upool.getAllUTXO().size();
                System.out.println("prevsize: " + prevsize);
                System.out.println("currsize: " + currsize);
                if (currsize != prevsize)
                    trns[i++] = a;
            }

        }
        //trns = upool.getAllUTXO().toArray(trns);
        return trns;
    }

}
