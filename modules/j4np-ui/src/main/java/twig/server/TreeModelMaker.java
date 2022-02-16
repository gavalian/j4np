/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author gavalian
 */
public class TreeModelMaker {
    
    private List<String> objectList = new ArrayList<>();
    
    public TreeModelMaker(){
        
    }
    
    public void setList(List<String> data){
        for(int i = 0; i < data.size(); i++){
            objectList.add(String.format("/root/%s", data.get(i)).replace("//", "/"));
        }
        Collections.sort(objectList);
    }
    
    public void show(){
        for(int i = 0; i < objectList.size(); i++){
            System.out.printf("%4d : %s\n",i,objectList.get(i));
        }
    }
    
    public DefaultMutableTreeNode getTreeModel(){
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
        printTree(root, objectList, "root", 1);
        return root;
    }
    
    public List<String> getSubs(List<String> list, String name, int num){
        Set<String> subs = new LinkedHashSet<>();
        for(int i = 0; i < list.size(); i++){
            String[] tokens = list.get(i).split("/");
            if(tokens.length>num+1){
                //System.out.println("oh yeah : " +  tokens[num]);
                if(tokens[num].compareTo(name)==0) subs.add(tokens[num+1]);
            }
        }
        List<String> result = new ArrayList<>();
        result.addAll(subs);
        return result;
    }
    
    public void printTree(DefaultMutableTreeNode node, List<String> list, String name, int num){        
        List<String> r = getSubs(list,name,num);        
        if(r.isEmpty()) return;
        for(int i = 0; i < r.size(); i++){
            String item = r.get(i);
            DefaultMutableTreeNode nNode = new DefaultMutableTreeNode(item);
            node.add(nNode);          
            //System.out.println("adding : " + item + " @ level " + num);
            this.printTree(nNode, list, item, num+1);
        }
    }
    
    
    public static void main(String[] args){
        
        TreeModelMaker tm = new TreeModelMaker();
        
        List<String> list =  Arrays.asList(
                "/a/b/c",
                "/a/b/d",
                "/a/b/e/f",
                "/a/b/g/h"
        );
        
        List<String> r = tm.getSubs(list, "a", 1);
        System.out.println("\na:\n" + Arrays.toString(r.toArray()));
        
        
        List<String> r2 = tm.getSubs(list, "b", 2);
        System.out.println("\nb:\n" + Arrays.toString(r2.toArray()));
        
        //String[] tokens = "/a/b/c/d/e/f".split("/");
        //System.out.println(Arrays.toString(tokens));
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("root");
        tm.printTree(node, list, "a", 1);
        System.out.println("done ");
        
        
        tm.setList(list);
        tm.show();
        tm.getTreeModel();
        
    }
}
