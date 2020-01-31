import sa.SaExp;
import sa.SaExpAdd;
import sa.SaNode;
import sc.analysis.DepthFirstAdapter;
import sc.node.APlusExp3;

public class Sc2sa extends DepthFirstAdapter {
    private SaNode returnValue;

    @Override
    public void caseAPlusExp3(APlusExp3 node) {
        SaExp op1, op2;
        node.getExp3().apply(this);
        op1 = (SaExp) this.returnValue;
        node.getExp4().apply(this);
        op2 = (SaExp) this.returnValue;
        this.returnValue = new SaExpAdd(op1, op2);
    }
}
