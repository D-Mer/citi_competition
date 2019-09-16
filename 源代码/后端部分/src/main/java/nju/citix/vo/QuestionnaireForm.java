package nju.citix.vo;

import lombok.Data;

import java.util.ArrayList;

/**
 * 问卷表单
 *
 * @author jiang hui
 * @date 2019/8/19
 */

@Data
public class QuestionnaireForm {

    /**
     * 消费者id
     */
    private Integer customerId;

    /**
     * 问题回答的列表
     * 共18个问题
     */
    private ArrayList<Character> answerList;

}
