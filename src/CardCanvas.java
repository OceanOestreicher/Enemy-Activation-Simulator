import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class CardCanvas extends JPanel {
    private final HashMap<RenderingHints.Key,Object> renderingHints;
    private Image currentCard;
    private int x = 0, y =0;
    public CardCanvas(){
        setIgnoreRepaint(true);
        renderingHints = new HashMap<>();
        renderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        renderingHints.put(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
    }
    public void clearCanvas(){
        currentCard = null;
        repaint();
    }
    public boolean isPainted(){
        return currentCard != null;
    }
    private boolean isDifferentOrientation(int cardWidth,int cardHeight){
        if(cardWidth < cardHeight && this.getWidth() > this.getHeight() || cardWidth > cardHeight && this.getWidth() < this.getHeight())return true;
        return false;
    }
    public void paintCard(Image card){
        //if width < height scale image differently
        int cardWidth = card.getWidth(null);
        int cardHeight = card.getHeight(null);
        if(isDifferentOrientation(cardWidth,cardHeight)){
            double widthToHeightRatio = (double)cardWidth/cardHeight;
            double heightToWidthRatio = (double)cardHeight/cardWidth;
            int width = (int)(this.getHeight()*widthToHeightRatio);
            int height = (int)(width*heightToWidthRatio);
            x = (this.getWidth() - width)/2;
            y = (this.getHeight() - height)/2;
            currentCard = card.getScaledInstance(width,height,Image.SCALE_SMOOTH);
        }
        else{
            currentCard = card.getScaledInstance(this.getWidth(),this.getHeight(),Image.SCALE_SMOOTH);
            x = 0;
            y = 0;
        }
        repaint();
    }
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHints(renderingHints);
        g2d.drawImage(currentCard,x,y,null);
        g2d.dispose();
    }
}
