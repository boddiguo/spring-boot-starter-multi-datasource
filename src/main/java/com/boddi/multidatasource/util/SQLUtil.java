/*
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.boddi.multidatasource.util;

import com.boddi.multidatasource.enums.SQLStatementType;
import com.boddi.multidatasource.exception.SQLParserException;

import com.alibaba.druid.sql.parser.Lexer;
import com.boddi.multidatasource.enums.SQLStatementType;
import com.google.common.base.CharMatcher;

import java.sql.SQLException;
import java.util.Collection;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * SQL工具类.
 * 
 * @author gaohongtao
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SQLUtil {
    
    /**
     * 去掉SQL表达式的特殊字符.
     * 
     * @param value SQL表达式
     * @return 去掉SQL特殊字符的表达式
     */
    public static String getExactlyValue(final String value) {
        return null == value ? null : CharMatcher.anyOf("[]`'\"").removeFrom(value);
    }

    /**
     * 安全的调用一组可能抛出{@linkplain SQLException}的对象中的方法.
     * 通过该方法保证后，保证每个对象中的方法均被调用一次
     *
     * @param throwableSQLExceptionObjects 调用方法可能抛出异常的对象集合
     * @param method 方法定义
     * @param <T> 对象类型
     * @throws SQLException 数据库访问异常会抛出
     */
    public static <T> void safeInvoke(final Collection<T> throwableSQLExceptionObjects, final ThrowableSQLExceptionMethod<T> method) throws SQLException {
        SQLException current = null;
        for (T each : throwableSQLExceptionObjects) {
            try {
                method.apply(each);
            } catch (final SQLException exp) {
                if (null == current) {
                    current = exp;
                } else {
                    current.setNextException(exp);
                    current = exp;
                }
            }
        }
        if (null != current) {
            throw current;
        }
    }

    /**
     * 根据SQL第一个单词判断SQL类型.
     * 
     * @param sql SQL语句
     * @return SQL类型
     */
    public static SQLStatementType getTypeByStart(final String sql) {
        //TODO: Use new Lexer Util.
        Lexer lexer = new Lexer(sql);
        lexer.nextToken();
        while (true) {
            switch (lexer.token()) {
                case SELECT: return SQLStatementType.SELECT;
                case INSERT: return SQLStatementType.INSERT;
                case UPDATE: return SQLStatementType.UPDATE;
                case DELETE: return SQLStatementType.DELETE;
                case EOF: throw new SQLParserException("Unsupported SQL statement: [%s]", sql);
                default: lexer.nextToken();
            }
        }
    }
}
