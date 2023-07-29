import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

public class CardCanvas extends JPanel {
    private final HashMap<RenderingHints.Key,Object> renderingHints;
    private Image currentCard,oneDamage,fiveDamage;
    private int x = 0, y =0,damage=0;
    public CardCanvas(Image[]damageGraphics,boolean enableDamage){
        renderingHints = new HashMap<>();
        renderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        renderingHints.put(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
        this.oneDamage =damageGraphics[0].getScaledInstance((int)(damageGraphics[0].getWidth(null)*.2),(int)(damageGraphics[0].getHeight(null)*.2),Image.SCALE_SMOOTH);
        this.fiveDamage = damageGraphics[1].getScaledInstance((int)(damageGraphics[1].getWidth(null)*.2),(int)(damageGraphics[1].getHeight(null)*.2),Image.SCALE_SMOOTH);
        if(enableDamage){
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if(isPainted()){
                        if(e.getButton() == 3){
                            if(damage > 0){
                                damage--;
                                paintCard(currentCard);
                            }
                        }
                        else{
                            if(damage < 78){
                                damage++;
                                paintCard(currentCard);
                            }
                        }

                    }

                }
            });
        }
    }
    public void clearCanvas(){
        currentCard = null;
        damage = 0;
        repaint();
    }
    public boolean isPainted(){
        return currentCard != null;
    }
    public int getDamage(){
        return damage;
    }
    public void setDamage(Integer damage){
        this.damage = (damage == null?0:damage);
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
        if(damage > 0){
            int row = 1,col=0;
            for(int d = 0; d<damage;){
                Image damageToDraw;
                if(damage -d >= 5){
                    damageToDraw = fiveDamage;
                    d+=5;
                }
                else{
                    damageToDraw = oneDamage;
                    d++;
                }
                if(damageToDraw.getWidth(null)*(col+1)>this.getWidth()){
                    row++;
                    col = 0;
                }
                g2d.drawImage(damageToDraw,(damageToDraw.getWidth(null)*col),(this.getHeight()/8)*row,null);
                col++;
            }
        }
        g2d.dispose();
    }
}
