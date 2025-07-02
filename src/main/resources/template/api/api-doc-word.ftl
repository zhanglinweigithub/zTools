<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<?mso-application progid="Word.Document"?>
<pkg:package xmlns:pkg="http://schemas.microsoft.com/office/2006/xmlPackage">
    <pkg:part pkg:name="/_rels/.rels" pkg:contentType="application/vnd.openxmlformats-package.relationships+xml">
        <pkg:xmlData>
            <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
                <Relationship Id="rId4"
                              Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument"
                              Target="word/document.xml"/>
                <Relationship Id="rId2"
                              Type="http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties"
                              Target="docProps/core.xml"/>
                <Relationship Id="rId1"
                              Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/extended-properties"
                              Target="docProps/app.xml"/>
                <Relationship Id="rId3"
                              Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/custom-properties"
                              Target="docProps/custom.xml"/>
            </Relationships>
        </pkg:xmlData>
    </pkg:part>
    <pkg:part pkg:name="/word/_rels/document.xml.rels"
              pkg:contentType="application/vnd.openxmlformats-package.relationships+xml">
        <pkg:xmlData>
            <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
                <Relationship Id="rId4"
                              Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/fontTable"
                              Target="fontTable.xml"/>
                <Relationship Id="rId3" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/theme"
                              Target="theme/theme1.xml"/>
                <Relationship Id="rId2"
                              Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/settings"
                              Target="settings.xml"/>
                <Relationship Id="rId1"
                              Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles"
                              Target="styles.xml"/>
            </Relationships>
        </pkg:xmlData>
    </pkg:part>
    <pkg:part pkg:name="/word/document.xml"
              pkg:contentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml">
        <pkg:xmlData>
            <w:document xmlns:wpc="http://schemas.microsoft.com/office/word/2010/wordprocessingCanvas"
                        xmlns:mc="http://schemas.openxmlformats.org/markup-compatibility/2006"
                        xmlns:o="urn:schemas-microsoft-com:office:office"
                        xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships"
                        xmlns:m="http://schemas.openxmlformats.org/officeDocument/2006/math"
                        xmlns:v="urn:schemas-microsoft-com:vml"
                        xmlns:wp14="http://schemas.microsoft.com/office/word/2010/wordprocessingDrawing"
                        xmlns:wp="http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing"
                        xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main"
                        xmlns:w14="http://schemas.microsoft.com/office/word/2010/wordml"
                        xmlns:w10="urn:schemas-microsoft-com:office:word"
                        xmlns:w15="http://schemas.microsoft.com/office/word/2012/wordml"
                        xmlns:wpg="http://schemas.microsoft.com/office/word/2010/wordprocessingGroup"
                        xmlns:wpi="http://schemas.microsoft.com/office/word/2010/wordprocessingInk"
                        xmlns:wne="http://schemas.microsoft.com/office/word/2006/wordml"
                        xmlns:wps="http://schemas.microsoft.com/office/word/2010/wordprocessingShape"
                        xmlns:wpsCustomData="http://www.wps.cn/officeDocument/2013/wpsCustomData"
                        mc:Ignorable="w14 w15 wp14">
                <w:body>
                    <w:p w14:paraId="3E92B2D9">
                        <w:pPr>
                            <w:pStyle w:val="2"/>
                            <w:bidi w:val="0"/>
                            <w:jc w:val="center"/>
                            <w:rPr>
                                <w:rFonts w:hint="eastAsia"/>
                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                            </w:rPr>
                        </w:pPr>
                        <w:r>
                            <w:rPr>
                                <w:rFonts w:hint="eastAsia"/>
                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                            </w:rPr>
                            <w:t>接口文档</w:t>
                        </w:r>
                    </w:p>
                    <#list apiList as api>
                    <w:p w14:paraId="03EA3CB0">
                        <w:pPr>
                            <w:pStyle w:val="3"/>
                            <w:keepNext/>
                            <w:keepLines/>
                            <w:pageBreakBefore w:val="0"/>
                            <w:widowControl w:val="0"/>
                            <w:numPr>
                                <w:numId w:val="0"/>
                            </w:numPr>
                            <w:kinsoku/>
                            <w:wordWrap/>
                            <w:overflowPunct/>
                            <w:topLinePunct w:val="0"/>
                            <w:autoSpaceDE/>
                            <w:autoSpaceDN/>
                            <w:bidi w:val="0"/>
                            <w:adjustRightInd/>
                            <w:snapToGrid/>
                            <w:spacing w:before="157" w:beforeLines="50" w:after="157" w:afterLines="50" w:line="360"
                                       w:lineRule="auto"/>
                            <w:textAlignment w:val="auto"/>
                            <w:rPr>
                                <w:rFonts w:hint="default" w:ascii="Times New Roman Regular"
                                          w:hAnsi="Times New Roman Regular" w:cs="Times New Roman Regular"/>
                                <w:sz w:val="32"/>
                                <w:szCs w:val="32"/>
                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                            </w:rPr>
                        </w:pPr>
                        <w:r>
                            <w:rPr>
                                <w:rFonts w:hint="eastAsia" w:ascii="Times New Roman Regular"
                                          w:hAnsi="Times New Roman Regular" w:cs="Times New Roman Regular"/>
                                <w:b w:val="0"/>
                                <w:sz w:val="32"/>
                                <w:szCs w:val="32"/>
                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                            </w:rPr>
                            <w:t>${api_index + 1}</w:t>
                        </w:r>
                        <w:r>
                            <w:rPr>
                                <w:rFonts w:hint="default" w:ascii="Times New Roman Regular"
                                          w:hAnsi="Times New Roman Regular" w:cs="Times New Roman Regular"/>
                                <w:b w:val="0"/>
                                <w:sz w:val="32"/>
                                <w:szCs w:val="32"/>
                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                            </w:rPr>
                            <w:t>、</w:t>
                        </w:r>
                        <w:r>
                            <w:rPr>
                                <w:rFonts w:hint="eastAsia" w:ascii="Times New Roman Regular"
                                          w:hAnsi="Times New Roman Regular" w:cs="Times New Roman Regular"/>
                                <w:b w:val="0"/>
                                <w:sz w:val="32"/>
                                <w:szCs w:val="32"/>
                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                            </w:rPr>
                            <w:t>${api.title!''}</w:t>
                        </w:r>
                    </w:p>
                    <w:p w14:paraId="4C260B24">
                        <w:pPr>
                            <w:pStyle w:val="4"/>
                            <w:keepNext/>
                            <w:keepLines/>
                            <w:pageBreakBefore w:val="0"/>
                            <w:widowControl w:val="0"/>
                            <w:kinsoku/>
                            <w:wordWrap/>
                            <w:overflowPunct/>
                            <w:topLinePunct w:val="0"/>
                            <w:autoSpaceDE/>
                            <w:autoSpaceDN/>
                            <w:bidi w:val="0"/>
                            <w:adjustRightInd/>
                            <w:snapToGrid/>
                            <w:spacing w:before="157" w:beforeLines="50" w:after="157" w:afterLines="50" w:line="360"
                                       w:lineRule="auto"/>
                            <w:textAlignment w:val="auto"/>
                            <w:rPr>
                                <w:rFonts w:hint="eastAsia" w:ascii="黑体" w:hAnsi="黑体" w:eastAsia="黑体"
                                          w:cs="黑体"/>
                                <w:b w:val="0"/>
                                <w:bCs/>
                                <w:sz w:val="28"/>
                                <w:szCs w:val="28"/>
                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                            </w:rPr>
                        </w:pPr>
                        <w:r>
                            <w:rPr>
                                <w:rFonts w:hint="eastAsia" w:ascii="黑体" w:hAnsi="黑体" w:eastAsia="黑体"
                                          w:cs="黑体"/>
                                <w:b w:val="0"/>
                                <w:bCs/>
                                <w:sz w:val="28"/>
                                <w:szCs w:val="28"/>
                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                            </w:rPr>
                            <w:t>基本信息</w:t>
                        </w:r>
                    </w:p>
                    <w:p w14:paraId="0807C4B8">
                        <w:pPr>
                            <w:keepNext w:val="0"/>
                            <w:keepLines w:val="0"/>
                            <w:pageBreakBefore w:val="0"/>
                            <w:widowControl w:val="0"/>
                            <w:numPr>
                                <w:numId w:val="0"/>
                            </w:numPr>
                            <w:kinsoku/>
                            <w:wordWrap/>
                            <w:overflowPunct/>
                            <w:topLinePunct w:val="0"/>
                            <w:autoSpaceDE/>
                            <w:autoSpaceDN/>
                            <w:bidi w:val="0"/>
                            <w:adjustRightInd/>
                            <w:snapToGrid/>
                            <w:ind w:leftChars="0"/>
                            <w:jc w:val="both"/>
                            <w:textAlignment w:val="auto"/>
                            <w:rPr>
                                <w:rFonts w:hint="default" w:ascii="宋体" w:hAnsi="宋体" w:eastAsia="宋体" w:cs="宋体"/>
                                <w:sz w:val="18"/>
                                <w:szCs w:val="18"/>
                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                            </w:rPr>
                        </w:pPr>
                        <w:r>
                            <w:rPr>
                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体" w:eastAsia="宋体"
                                          w:cs="宋体"/>
                                <w:b/>
                                <w:bCs/>
                                <w:sz w:val="18"/>
                                <w:szCs w:val="18"/>
                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                            </w:rPr>
                            <w:t>·</w:t>
                        </w:r>
                        <w:r>
                            <w:rPr>
                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体" w:eastAsia="宋体"
                                          w:cs="宋体"/>
                                <w:sz w:val="18"/>
                                <w:szCs w:val="18"/>
                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                            </w:rPr>
                            <w:t>请求方式：</w:t>
                        </w:r>
                        <w:r>
                            <w:rPr>
                                <w:rFonts w:hint="default" w:ascii="Times New Roman Regular"
                                          w:hAnsi="Times New Roman Regular" w:eastAsia="宋体"
                                          w:cs="Times New Roman Regular"/>
                                <w:sz w:val="18"/>
                                <w:szCs w:val="18"/>
                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                            </w:rPr>
                            <w:t>${api.baseInfo.requestType!''}</w:t>
                        </w:r>
                    </w:p>
                    <w:p w14:paraId="4AA329CB">
                        <w:pPr>
                            <w:keepNext w:val="0"/>
                            <w:keepLines w:val="0"/>
                            <w:pageBreakBefore w:val="0"/>
                            <w:widowControl w:val="0"/>
                            <w:numPr>
                                <w:ilvl w:val="0"/>
                                <w:numId w:val="0"/>
                            </w:numPr>
                            <w:kinsoku/>
                            <w:wordWrap/>
                            <w:overflowPunct/>
                            <w:topLinePunct w:val="0"/>
                            <w:autoSpaceDE/>
                            <w:autoSpaceDN/>
                            <w:bidi w:val="0"/>
                            <w:adjustRightInd/>
                            <w:snapToGrid/>
                            <w:ind w:leftChars="0"/>
                            <w:jc w:val="both"/>
                            <w:textAlignment w:val="auto"/>
                            <w:rPr>
                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体" w:eastAsia="宋体"
                                          w:cs="宋体"/>
                                <w:sz w:val="18"/>
                                <w:szCs w:val="18"/>
                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                            </w:rPr>
                        </w:pPr>
                        <w:r>
                            <w:rPr>
                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体" w:eastAsia="宋体"
                                          w:cs="宋体"/>
                                <w:b/>
                                <w:bCs/>
                                <w:sz w:val="18"/>
                                <w:szCs w:val="18"/>
                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                            </w:rPr>
                            <w:t>·</w:t>
                        </w:r>
                        <w:r>
                            <w:rPr>
                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体" w:eastAsia="宋体"
                                          w:cs="宋体"/>
                                <w:sz w:val="18"/>
                                <w:szCs w:val="18"/>
                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                            </w:rPr>
                            <w:t>接口路径：</w:t>
                        </w:r>
                        <w:r>
                            <w:rPr>
                                <w:rFonts w:hint="default" w:ascii="Times New Roman Regular"
                                          w:hAnsi="Times New Roman Regular" w:eastAsia="宋体"
                                          w:cs="Times New Roman Regular"/>
                                <w:sz w:val="18"/>
                                <w:szCs w:val="18"/>
                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                            </w:rPr>
                            <w:t>${requestPrefix!''}${api.baseInfo.requestPath!''}</w:t>
                        </w:r>
                    </w:p>
                    <w:p w14:paraId="6A1B0C49">
                        <w:pPr>
                            <w:keepNext w:val="0"/>
                            <w:keepLines w:val="0"/>
                            <w:pageBreakBefore w:val="0"/>
                            <w:widowControl w:val="0"/>
                            <w:numPr>
                                <w:ilvl w:val="0"/>
                                <w:numId w:val="0"/>
                            </w:numPr>
                            <w:kinsoku/>
                            <w:wordWrap/>
                            <w:overflowPunct/>
                            <w:topLinePunct w:val="0"/>
                            <w:autoSpaceDE/>
                            <w:autoSpaceDN/>
                            <w:bidi w:val="0"/>
                            <w:adjustRightInd/>
                            <w:snapToGrid/>
                            <w:ind w:leftChars="0"/>
                            <w:jc w:val="both"/>
                            <w:textAlignment w:val="auto"/>
                            <w:rPr>
                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体" w:eastAsia="宋体"
                                          w:cs="宋体"/>
                                <w:sz w:val="18"/>
                                <w:szCs w:val="18"/>
                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                            </w:rPr>
                        </w:pPr>
                        <w:r>
                            <w:rPr>
                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体" w:eastAsia="宋体"
                                          w:cs="宋体"/>
                                <w:b/>
                                <w:bCs/>
                                <w:sz w:val="18"/>
                                <w:szCs w:val="18"/>
                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                            </w:rPr>
                            <w:t>·</w:t>
                        </w:r>
                        <w:r>
                            <w:rPr>
                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体" w:eastAsia="宋体"
                                          w:cs="宋体"/>
                                <w:sz w:val="18"/>
                                <w:szCs w:val="18"/>
                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                            </w:rPr>
                            <w:t>接口描述：</w:t>
                        </w:r>
                        <w:r>
                            <w:rPr>
                                <w:rFonts w:hint="default" w:ascii="Times New Roman Regular"
                                          w:hAnsi="Times New Roman Regular" w:eastAsia="宋体"
                                          w:cs="Times New Roman Regular"/>
                                <w:sz w:val="18"/>
                                <w:szCs w:val="18"/>
                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                            </w:rPr>
                            <w:t>${api.baseInfo.description!''}</w:t>
                        </w:r>
                    </w:p>
                    <w:p w14:paraId="3EAB2FD2">
                        <w:pPr>
                            <w:pStyle w:val="4"/>
                            <w:keepNext/>
                            <w:keepLines/>
                            <w:pageBreakBefore w:val="0"/>
                            <w:widowControl w:val="0"/>
                            <w:kinsoku/>
                            <w:wordWrap/>
                            <w:overflowPunct/>
                            <w:topLinePunct w:val="0"/>
                            <w:autoSpaceDE/>
                            <w:autoSpaceDN/>
                            <w:bidi w:val="0"/>
                            <w:adjustRightInd/>
                            <w:snapToGrid/>
                            <w:spacing w:before="157" w:beforeLines="50" w:after="157" w:afterLines="50" w:line="360"
                                       w:lineRule="auto"/>
                            <w:textAlignment w:val="auto"/>
                            <w:rPr>
                                <w:rFonts w:hint="eastAsia" w:ascii="黑体" w:hAnsi="黑体" w:eastAsia="黑体"
                                          w:cs="黑体"/>
                                <w:b w:val="0"/>
                                <w:bCs/>
                                <w:sz w:val="28"/>
                                <w:szCs w:val="28"/>
                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                            </w:rPr>
                        </w:pPr>
                        <w:r>
                            <w:rPr>
                                <w:rFonts w:hint="eastAsia" w:ascii="黑体" w:hAnsi="黑体" w:eastAsia="黑体"
                                          w:cs="黑体"/>
                                <w:b w:val="0"/>
                                <w:bCs/>
                                <w:sz w:val="28"/>
                                <w:szCs w:val="28"/>
                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                            </w:rPr>
                            <w:t>请求参数</w:t>
                        </w:r>
                    </w:p>
                    <#if api.requestInfo??>
                    <#if api.requestInfo.requestHeader?? && api.requestInfo.requestHeader.rowList?? && api.requestInfo.requestHeader.rowList?has_content>
                        <w:p w14:paraId="05C67261">
                            <w:pPr>
                                <w:pStyle w:val="5"/>
                                <w:keepNext/>
                                <w:keepLines/>
                                <w:pageBreakBefore w:val="0"/>
                                <w:widowControl w:val="0"/>
                                <w:kinsoku/>
                                <w:wordWrap/>
                                <w:overflowPunct/>
                                <w:topLinePunct w:val="0"/>
                                <w:autoSpaceDE/>
                                <w:autoSpaceDN/>
                                <w:bidi w:val="0"/>
                                <w:adjustRightInd/>
                                <w:snapToGrid/>
                                <w:spacing w:before="157" w:beforeLines="50" w:after="157" w:afterLines="50"
                                           w:line="360"
                                           w:lineRule="auto"/>
                                <w:textAlignment w:val="auto"/>
                                <w:rPr>
                                    <w:rFonts w:hint="eastAsia"/>
                                    <w:b w:val="0"/>
                                    <w:bCs/>
                                    <w:sz w:val="24"/>
                                    <w:szCs w:val="24"/>
                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                </w:rPr>
                            </w:pPr>
                            <w:r>
                                <w:rPr>
                                    <w:rFonts w:hint="eastAsia"/>
                                    <w:b w:val="0"/>
                                    <w:bCs/>
                                    <w:sz w:val="24"/>
                                    <w:szCs w:val="24"/>
                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                </w:rPr>
                                <w:t>请求头参数（</w:t>
                            </w:r>
                            <w:r>
                                <w:rPr>
                                    <w:rFonts w:hint="default" w:ascii="Times New Roman Regular"
                                              w:hAnsi="Times New Roman Regular" w:cs="Times New Roman Regular"/>
                                    <w:b w:val="0"/>
                                    <w:bCs/>
                                    <w:sz w:val="24"/>
                                    <w:szCs w:val="24"/>
                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                </w:rPr>
                                <w:t>RequestHeader</w:t>
                            </w:r>
                            <w:r>
                                <w:rPr>
                                    <w:rFonts w:hint="eastAsia"/>
                                    <w:b w:val="0"/>
                                    <w:bCs/>
                                    <w:sz w:val="24"/>
                                    <w:szCs w:val="24"/>
                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                </w:rPr>
                                <w:t>）</w:t>
                            </w:r>
                        </w:p>
                        <w:tbl>
                            <w:tblPr>
                                <w:tblStyle w:val="8"/>
                                <w:tblW w:w="0" w:type="auto"/>
                                <w:tblInd w:w="0" w:type="dxa"/>
                                <w:tblBorders>
                                    <w:top w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                                    <w:left w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                                    <w:bottom w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                                    <w:right w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                                    <w:insideH w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                                    <w:insideV w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                                </w:tblBorders>
                                <w:tblLayout w:type="autofit"/>
                                <w:tblCellMar>
                                    <w:top w:w="0" w:type="dxa"/>
                                    <w:left w:w="108" w:type="dxa"/>
                                    <w:bottom w:w="0" w:type="dxa"/>
                                    <w:right w:w="108" w:type="dxa"/>
                                </w:tblCellMar>
                            </w:tblPr>
                            <w:tblGrid>
                                <w:gridCol w:w="1704"/>
                                <w:gridCol w:w="1704"/>
                                <w:gridCol w:w="1704"/>
                                <w:gridCol w:w="1705"/>
                                <w:gridCol w:w="1705"/>
                            </w:tblGrid>
                            <w:tr w14:paraId="6017F663">
                                <w:tc>
                                    <w:tcPr>
                                        <w:tcW w:w="1704" w:type="dxa"/>
                                        <w:shd w:val="clear" w:color="auto" w:fill="E7E6E6"/>
                                    </w:tcPr>
                                    <w:p w14:paraId="1C8E03C0">
                                        <w:pPr>
                                            <w:widowControl w:val="0"/>
                                            <w:numPr>
                                                <w:ilvl w:val="0"/>
                                                <w:numId w:val="0"/>
                                            </w:numPr>
                                            <w:jc w:val="center"/>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                        </w:pPr>
                                        <w:r>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                            <w:t>名称</w:t>
                                        </w:r>
                                    </w:p>
                                </w:tc>
                                <w:tc>
                                    <w:tcPr>
                                        <w:tcW w:w="1704" w:type="dxa"/>
                                        <w:shd w:val="clear" w:color="auto" w:fill="E7E6E6"/>
                                    </w:tcPr>
                                    <w:p w14:paraId="219C9B91">
                                        <w:pPr>
                                            <w:widowControl w:val="0"/>
                                            <w:numPr>
                                                <w:ilvl w:val="0"/>
                                                <w:numId w:val="0"/>
                                            </w:numPr>
                                            <w:jc w:val="center"/>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                        </w:pPr>
                                        <w:r>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                            <w:t>类型</w:t>
                                        </w:r>
                                    </w:p>
                                </w:tc>
                                <w:tc>
                                    <w:tcPr>
                                        <w:tcW w:w="1704" w:type="dxa"/>
                                        <w:shd w:val="clear" w:color="auto" w:fill="E7E6E6"/>
                                    </w:tcPr>
                                    <w:p w14:paraId="1FD577E3">
                                        <w:pPr>
                                            <w:widowControl w:val="0"/>
                                            <w:numPr>
                                                <w:ilvl w:val="0"/>
                                                <w:numId w:val="0"/>
                                            </w:numPr>
                                            <w:jc w:val="center"/>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                        </w:pPr>
                                        <w:r>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                            <w:t>必填</w:t>
                                        </w:r>
                                    </w:p>
                                </w:tc>
                                <w:tc>
                                    <w:tcPr>
                                        <w:tcW w:w="1705" w:type="dxa"/>
                                        <w:shd w:val="clear" w:color="auto" w:fill="E7E6E6"/>
                                    </w:tcPr>
                                    <w:p w14:paraId="749EB082">
                                        <w:pPr>
                                            <w:widowControl w:val="0"/>
                                            <w:numPr>
                                                <w:ilvl w:val="0"/>
                                                <w:numId w:val="0"/>
                                            </w:numPr>
                                            <w:jc w:val="center"/>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                        </w:pPr>
                                        <w:r>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                            <w:t>说明</w:t>
                                        </w:r>
                                    </w:p>
                                </w:tc>
                                <w:tc>
                                    <w:tcPr>
                                        <w:tcW w:w="1705" w:type="dxa"/>
                                        <w:shd w:val="clear" w:color="auto" w:fill="E7E6E6"/>
                                    </w:tcPr>
                                    <w:p w14:paraId="4C380940">
                                        <w:pPr>
                                            <w:widowControl w:val="0"/>
                                            <w:numPr>
                                                <w:ilvl w:val="0"/>
                                                <w:numId w:val="0"/>
                                            </w:numPr>
                                            <w:jc w:val="center"/>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                        </w:pPr>
                                        <w:r>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                            <w:t>示例</w:t>
                                        </w:r>
                                    </w:p>
                                </w:tc>
                            </w:tr>
                            <#list api.requestInfo.requestHeader.rowList as rowInfo>
                                <w:tr w14:paraId="0549D77C">
                                    <w:tc>
                                        <w:tcPr>
                                            <w:tcW w:w="1704" w:type="dxa"/>
                                        </w:tcPr>
                                        <w:p w14:paraId="0BCE9A5E">
                                            <w:pPr>
                                                <w:widowControl w:val="0"/>
                                                <w:numPr>
                                                    <w:numId w:val="0"/>
                                                </w:numPr>
                                                <w:jc w:val="center"/>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                            </w:pPr>
                                            <w:r>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                                <w:t>${rowInfo.name!''}</w:t>
                                            </w:r>
                                        </w:p>
                                    </w:tc>
                                    <w:tc>
                                        <w:tcPr>
                                            <w:tcW w:w="1704" w:type="dxa"/>
                                        </w:tcPr>
                                        <w:p w14:paraId="58D17A02">
                                            <w:pPr>
                                                <w:widowControl w:val="0"/>
                                                <w:numPr>
                                                    <w:numId w:val="0"/>
                                                </w:numPr>
                                                <w:jc w:val="center"/>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                            </w:pPr>
                                            <w:r>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                                <w:t>${rowInfo.type!''}</w:t>
                                            </w:r>
                                        </w:p>
                                    </w:tc>
                                    <w:tc>
                                        <w:tcPr>
                                            <w:tcW w:w="1704" w:type="dxa"/>
                                        </w:tcPr>
                                        <w:p w14:paraId="4437FEA9">
                                            <w:pPr>
                                                <w:widowControl w:val="0"/>
                                                <w:numPr>
                                                    <w:numId w:val="0"/>
                                                </w:numPr>
                                                <w:jc w:val="center"/>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                            </w:pPr>
                                            <w:r>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                                <w:t>${rowInfo.required?then('Y', 'N')}</w:t>
                                            </w:r>
                                        </w:p>
                                    </w:tc>
                                    <w:tc>
                                        <w:tcPr>
                                            <w:tcW w:w="1705" w:type="dxa"/>
                                        </w:tcPr>
                                        <w:p w14:paraId="16A981BD">
                                            <w:pPr>
                                                <w:widowControl w:val="0"/>
                                                <w:numPr>
                                                    <w:numId w:val="0"/>
                                                </w:numPr>
                                                <w:jc w:val="center"/>
                                                <w:rPr>
                                                    <w:rFonts w:hint="default" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                            </w:pPr>
                                            <w:r>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                                <w:t>${rowInfo.description!''}</w:t>
                                            </w:r>
                                        </w:p>
                                    </w:tc>
                                    <w:tc>
                                        <w:tcPr>
                                            <w:tcW w:w="1705" w:type="dxa"/>
                                        </w:tcPr>
                                        <w:p w14:paraId="4D4CC886">
                                            <w:pPr>
                                                <w:widowControl w:val="0"/>
                                                <w:numPr>
                                                    <w:numId w:val="0"/>
                                                </w:numPr>
                                                <w:jc w:val="center"/>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                            </w:pPr>
                                            <w:r>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                                <w:t>${rowInfo.example!''}</w:t>
                                            </w:r>
                                        </w:p>
                                    </w:tc>
                                </w:tr>
                            </#list>
                        </w:tbl>
                    </#if>
                    <#if api.requestInfo.pathVariable?? && api.requestInfo.pathVariable.rowList?? && api.requestInfo.pathVariable.rowList?has_content>
                        <w:p w14:paraId="37071BE1">
                            <w:pPr>
                                <w:pStyle w:val="5"/>
                                <w:keepNext/>
                                <w:keepLines/>
                                <w:pageBreakBefore w:val="0"/>
                                <w:widowControl w:val="0"/>
                                <w:kinsoku/>
                                <w:wordWrap/>
                                <w:overflowPunct/>
                                <w:topLinePunct w:val="0"/>
                                <w:autoSpaceDE/>
                                <w:autoSpaceDN/>
                                <w:bidi w:val="0"/>
                                <w:adjustRightInd/>
                                <w:snapToGrid/>
                                <w:spacing w:before="157" w:beforeLines="50" w:after="157" w:afterLines="50"
                                           w:line="360"
                                           w:lineRule="auto"/>
                                <w:textAlignment w:val="auto"/>
                                <w:rPr>
                                    <w:rFonts w:hint="eastAsia"/>
                                    <w:b w:val="0"/>
                                    <w:bCs/>
                                    <w:sz w:val="24"/>
                                    <w:szCs w:val="24"/>
                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                </w:rPr>
                            </w:pPr>
                            <w:r>
                                <w:rPr>
                                    <w:rFonts w:hint="eastAsia"/>
                                    <w:b w:val="0"/>
                                    <w:bCs/>
                                    <w:sz w:val="24"/>
                                    <w:szCs w:val="24"/>
                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                </w:rPr>
                                <w:t>路径参数（</w:t>
                            </w:r>
                            <w:r>
                                <w:rPr>
                                    <w:rFonts w:hint="default" w:ascii="Times New Roman Regular"
                                              w:hAnsi="Times New Roman Regular" w:cs="Times New Roman Regular"/>
                                    <w:b w:val="0"/>
                                    <w:bCs/>
                                    <w:sz w:val="24"/>
                                    <w:szCs w:val="24"/>
                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                </w:rPr>
                                <w:t>PathVariable</w:t>
                            </w:r>
                            <w:r>
                                <w:rPr>
                                    <w:rFonts w:hint="eastAsia"/>
                                    <w:b w:val="0"/>
                                    <w:bCs/>
                                    <w:sz w:val="24"/>
                                    <w:szCs w:val="24"/>
                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                </w:rPr>
                                <w:t>）</w:t>
                            </w:r>
                        </w:p>
                        <w:tbl>
                            <w:tblPr>
                                <w:tblStyle w:val="8"/>
                                <w:tblW w:w="0" w:type="auto"/>
                                <w:tblInd w:w="0" w:type="dxa"/>
                                <w:tblBorders>
                                    <w:top w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                                    <w:left w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                                    <w:bottom w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                                    <w:right w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                                    <w:insideH w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                                    <w:insideV w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                                </w:tblBorders>
                                <w:tblLayout w:type="autofit"/>
                                <w:tblCellMar>
                                    <w:top w:w="0" w:type="dxa"/>
                                    <w:left w:w="108" w:type="dxa"/>
                                    <w:bottom w:w="0" w:type="dxa"/>
                                    <w:right w:w="108" w:type="dxa"/>
                                </w:tblCellMar>
                            </w:tblPr>
                            <w:tblGrid>
                                <w:gridCol w:w="1704"/>
                                <w:gridCol w:w="1704"/>
                                <w:gridCol w:w="1704"/>
                                <w:gridCol w:w="1705"/>
                                <w:gridCol w:w="1705"/>
                            </w:tblGrid>
                            <w:tr w14:paraId="1B1CA620">
                                <w:tc>
                                    <w:tcPr>
                                        <w:tcW w:w="1704" w:type="dxa"/>
                                        <w:shd w:val="clear" w:color="auto" w:fill="E7E6E6"/>
                                    </w:tcPr>
                                    <w:p w14:paraId="029AD0D1">
                                        <w:pPr>
                                            <w:widowControl w:val="0"/>
                                            <w:numPr>
                                                <w:ilvl w:val="0"/>
                                                <w:numId w:val="0"/>
                                            </w:numPr>
                                            <w:jc w:val="center"/>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                        </w:pPr>
                                        <w:r>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                            <w:t>名称</w:t>
                                        </w:r>
                                    </w:p>
                                </w:tc>
                                <w:tc>
                                    <w:tcPr>
                                        <w:tcW w:w="1704" w:type="dxa"/>
                                        <w:shd w:val="clear" w:color="auto" w:fill="E7E6E6"/>
                                    </w:tcPr>
                                    <w:p w14:paraId="2A70BF5B">
                                        <w:pPr>
                                            <w:widowControl w:val="0"/>
                                            <w:numPr>
                                                <w:ilvl w:val="0"/>
                                                <w:numId w:val="0"/>
                                            </w:numPr>
                                            <w:jc w:val="center"/>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                        </w:pPr>
                                        <w:r>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                            <w:t>类型</w:t>
                                        </w:r>
                                    </w:p>
                                </w:tc>
                                <w:tc>
                                    <w:tcPr>
                                        <w:tcW w:w="1704" w:type="dxa"/>
                                        <w:shd w:val="clear" w:color="auto" w:fill="E7E6E6"/>
                                    </w:tcPr>
                                    <w:p w14:paraId="78AA4E65">
                                        <w:pPr>
                                            <w:widowControl w:val="0"/>
                                            <w:numPr>
                                                <w:ilvl w:val="0"/>
                                                <w:numId w:val="0"/>
                                            </w:numPr>
                                            <w:jc w:val="center"/>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                        </w:pPr>
                                        <w:r>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                            <w:t>必填</w:t>
                                        </w:r>
                                    </w:p>
                                </w:tc>
                                <w:tc>
                                    <w:tcPr>
                                        <w:tcW w:w="1705" w:type="dxa"/>
                                        <w:shd w:val="clear" w:color="auto" w:fill="E7E6E6"/>
                                    </w:tcPr>
                                    <w:p w14:paraId="6E653546">
                                        <w:pPr>
                                            <w:widowControl w:val="0"/>
                                            <w:numPr>
                                                <w:ilvl w:val="0"/>
                                                <w:numId w:val="0"/>
                                            </w:numPr>
                                            <w:jc w:val="center"/>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                        </w:pPr>
                                        <w:r>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                            <w:t>说明</w:t>
                                        </w:r>
                                    </w:p>
                                </w:tc>
                                <w:tc>
                                    <w:tcPr>
                                        <w:tcW w:w="1705" w:type="dxa"/>
                                        <w:shd w:val="clear" w:color="auto" w:fill="E7E6E6"/>
                                    </w:tcPr>
                                    <w:p w14:paraId="115F61DF">
                                        <w:pPr>
                                            <w:widowControl w:val="0"/>
                                            <w:numPr>
                                                <w:ilvl w:val="0"/>
                                                <w:numId w:val="0"/>
                                            </w:numPr>
                                            <w:jc w:val="center"/>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                        </w:pPr>
                                        <w:r>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                            <w:t>示例</w:t>
                                        </w:r>
                                    </w:p>
                                </w:tc>
                            </w:tr>
                            <#list api.requestInfo.pathVariable.rowList as rowInfo>
                                <w:tr w14:paraId="1415A6E9">
                                    <w:trPr>
                                        <w:jc w:val="center"/>
                                    </w:trPr>
                                    <w:tc>
                                        <w:tcPr>
                                            <w:tcW w:w="1704" w:type="dxa"/>
                                        </w:tcPr>
                                        <w:p w14:paraId="22F0A040">
                                            <w:pPr>
                                                <w:widowControl w:val="0"/>
                                                <w:numPr>
                                                    <w:ilvl w:val="0"/>
                                                    <w:numId w:val="0"/>
                                                </w:numPr>
                                                <w:jc w:val="center"/>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                            </w:pPr>
                                            <w:r>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                                <w:t>${rowInfo.name!''}</w:t>
                                            </w:r>
                                        </w:p>
                                    </w:tc>
                                    <w:tc>
                                        <w:tcPr>
                                            <w:tcW w:w="1704" w:type="dxa"/>
                                        </w:tcPr>
                                        <w:p w14:paraId="1FBF58F0">
                                            <w:pPr>
                                                <w:widowControl w:val="0"/>
                                                <w:numPr>
                                                    <w:ilvl w:val="0"/>
                                                    <w:numId w:val="0"/>
                                                </w:numPr>
                                                <w:jc w:val="center"/>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                            </w:pPr>
                                            <w:r>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                                <w:t>${rowInfo.type!''}</w:t>
                                            </w:r>
                                        </w:p>
                                    </w:tc>
                                    <w:tc>
                                        <w:tcPr>
                                            <w:tcW w:w="1704" w:type="dxa"/>
                                        </w:tcPr>
                                        <w:p w14:paraId="7DD440BA">
                                            <w:pPr>
                                                <w:widowControl w:val="0"/>
                                                <w:numPr>
                                                    <w:ilvl w:val="0"/>
                                                    <w:numId w:val="0"/>
                                                </w:numPr>
                                                <w:jc w:val="center"/>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                            </w:pPr>
                                            <w:r>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                                <w:t>${rowInfo.required?then('Y', 'N')}</w:t>
                                            </w:r>
                                        </w:p>
                                    </w:tc>
                                    <w:tc>
                                        <w:tcPr>
                                            <w:tcW w:w="1705" w:type="dxa"/>
                                        </w:tcPr>
                                        <w:p w14:paraId="5BB66F94">
                                            <w:pPr>
                                                <w:widowControl w:val="0"/>
                                                <w:numPr>
                                                    <w:ilvl w:val="0"/>
                                                    <w:numId w:val="0"/>
                                                </w:numPr>
                                                <w:jc w:val="center"/>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                            </w:pPr>
                                            <w:r>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                                <w:t>${rowInfo.description!''}</w:t>
                                            </w:r>
                                        </w:p>
                                    </w:tc>
                                    <w:tc>
                                        <w:tcPr>
                                            <w:tcW w:w="1705" w:type="dxa"/>
                                        </w:tcPr>
                                        <w:p w14:paraId="68FA7C81">
                                            <w:pPr>
                                                <w:widowControl w:val="0"/>
                                                <w:numPr>
                                                    <w:ilvl w:val="0"/>
                                                    <w:numId w:val="0"/>
                                                </w:numPr>
                                                <w:jc w:val="center"/>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                            </w:pPr>
                                            <w:r>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                                <w:t>${rowInfo.example!''}</w:t>
                                            </w:r>
                                        </w:p>
                                    </w:tc>
                                </w:tr>
                            </#list>
                        </w:tbl>
                    </#if>
                    <#if api.requestInfo.requestParam?? && api.requestInfo.requestParam.rowList?? && api.requestInfo.requestParam.rowList?has_content>
                        <w:p w14:paraId="3F613119">
                            <w:pPr>
                                <w:pStyle w:val="5"/>
                                <w:keepNext/>
                                <w:keepLines/>
                                <w:pageBreakBefore w:val="0"/>
                                <w:widowControl w:val="0"/>
                                <w:kinsoku/>
                                <w:wordWrap/>
                                <w:overflowPunct/>
                                <w:topLinePunct w:val="0"/>
                                <w:autoSpaceDE/>
                                <w:autoSpaceDN/>
                                <w:bidi w:val="0"/>
                                <w:adjustRightInd/>
                                <w:snapToGrid/>
                                <w:spacing w:before="157" w:beforeLines="50" w:after="157" w:afterLines="50"
                                           w:line="360"
                                           w:lineRule="auto"/>
                                <w:textAlignment w:val="auto"/>
                                <w:rPr>
                                    <w:rFonts w:hint="eastAsia"/>
                                    <w:b w:val="0"/>
                                    <w:bCs/>
                                    <w:sz w:val="24"/>
                                    <w:szCs w:val="24"/>
                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                </w:rPr>
                            </w:pPr>
                            <w:r>
                                <w:rPr>
                                    <w:rFonts w:hint="eastAsia"/>
                                    <w:b w:val="0"/>
                                    <w:bCs/>
                                    <w:sz w:val="24"/>
                                    <w:szCs w:val="24"/>
                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                </w:rPr>
                                <w:t>查询参数（</w:t>
                            </w:r>
                            <w:r>
                                <w:rPr>
                                    <w:rFonts w:hint="default" w:ascii="Times New Roman Regular"
                                              w:hAnsi="Times New Roman Regular" w:cs="Times New Roman Regular"/>
                                    <w:b w:val="0"/>
                                    <w:bCs/>
                                    <w:sz w:val="24"/>
                                    <w:szCs w:val="24"/>
                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                </w:rPr>
                                <w:t>RequestParam</w:t>
                            </w:r>
                            <w:r>
                                <w:rPr>
                                    <w:rFonts w:hint="eastAsia"/>
                                    <w:b w:val="0"/>
                                    <w:bCs/>
                                    <w:sz w:val="24"/>
                                    <w:szCs w:val="24"/>
                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                </w:rPr>
                                <w:t>）</w:t>
                            </w:r>
                        </w:p>
                        <w:tbl>
                            <w:tblPr>
                                <w:tblStyle w:val="8"/>
                                <w:tblW w:w="0" w:type="auto"/>
                                <w:tblInd w:w="0" w:type="dxa"/>
                                <w:tblBorders>
                                    <w:top w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                                    <w:left w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                                    <w:bottom w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                                    <w:right w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                                    <w:insideH w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                                    <w:insideV w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                                </w:tblBorders>
                                <w:tblLayout w:type="autofit"/>
                                <w:tblCellMar>
                                    <w:top w:w="0" w:type="dxa"/>
                                    <w:left w:w="108" w:type="dxa"/>
                                    <w:bottom w:w="0" w:type="dxa"/>
                                    <w:right w:w="108" w:type="dxa"/>
                                </w:tblCellMar>
                            </w:tblPr>
                            <w:tblGrid>
                                <w:gridCol w:w="1704"/>
                                <w:gridCol w:w="1704"/>
                                <w:gridCol w:w="1704"/>
                                <w:gridCol w:w="1705"/>
                                <w:gridCol w:w="1705"/>
                            </w:tblGrid>
                            <w:tr w14:paraId="4BD930A4">
                                <w:tc>
                                    <w:tcPr>
                                        <w:tcW w:w="1704" w:type="dxa"/>
                                        <w:shd w:val="clear" w:color="auto" w:fill="E7E6E6"/>
                                    </w:tcPr>
                                    <w:p w14:paraId="3925C9AE">
                                        <w:pPr>
                                            <w:widowControl w:val="0"/>
                                            <w:numPr>
                                                <w:ilvl w:val="0"/>
                                                <w:numId w:val="0"/>
                                            </w:numPr>
                                            <w:jc w:val="center"/>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                        </w:pPr>
                                        <w:r>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                            <w:t>名称</w:t>
                                        </w:r>
                                    </w:p>
                                </w:tc>
                                <w:tc>
                                    <w:tcPr>
                                        <w:tcW w:w="1704" w:type="dxa"/>
                                        <w:shd w:val="clear" w:color="auto" w:fill="E7E6E6"/>
                                    </w:tcPr>
                                    <w:p w14:paraId="4024C19C">
                                        <w:pPr>
                                            <w:widowControl w:val="0"/>
                                            <w:numPr>
                                                <w:ilvl w:val="0"/>
                                                <w:numId w:val="0"/>
                                            </w:numPr>
                                            <w:jc w:val="center"/>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                        </w:pPr>
                                        <w:r>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                            <w:t>类型</w:t>
                                        </w:r>
                                    </w:p>
                                </w:tc>
                                <w:tc>
                                    <w:tcPr>
                                        <w:tcW w:w="1704" w:type="dxa"/>
                                        <w:shd w:val="clear" w:color="auto" w:fill="E7E6E6"/>
                                    </w:tcPr>
                                    <w:p w14:paraId="07B2D61F">
                                        <w:pPr>
                                            <w:widowControl w:val="0"/>
                                            <w:numPr>
                                                <w:ilvl w:val="0"/>
                                                <w:numId w:val="0"/>
                                            </w:numPr>
                                            <w:jc w:val="center"/>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                        </w:pPr>
                                        <w:r>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                            <w:t>必填</w:t>
                                        </w:r>
                                    </w:p>
                                </w:tc>
                                <w:tc>
                                    <w:tcPr>
                                        <w:tcW w:w="1705" w:type="dxa"/>
                                        <w:shd w:val="clear" w:color="auto" w:fill="E7E6E6"/>
                                    </w:tcPr>
                                    <w:p w14:paraId="4FA5FC18">
                                        <w:pPr>
                                            <w:widowControl w:val="0"/>
                                            <w:numPr>
                                                <w:ilvl w:val="0"/>
                                                <w:numId w:val="0"/>
                                            </w:numPr>
                                            <w:jc w:val="center"/>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                        </w:pPr>
                                        <w:r>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                            <w:t>说明</w:t>
                                        </w:r>
                                    </w:p>
                                </w:tc>
                                <w:tc>
                                    <w:tcPr>
                                        <w:tcW w:w="1705" w:type="dxa"/>
                                        <w:shd w:val="clear" w:color="auto" w:fill="E7E6E6"/>
                                    </w:tcPr>
                                    <w:p w14:paraId="62CDBEE2">
                                        <w:pPr>
                                            <w:widowControl w:val="0"/>
                                            <w:numPr>
                                                <w:ilvl w:val="0"/>
                                                <w:numId w:val="0"/>
                                            </w:numPr>
                                            <w:jc w:val="center"/>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                        </w:pPr>
                                        <w:r>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                            <w:t>示例</w:t>
                                        </w:r>
                                    </w:p>
                                </w:tc>
                            </w:tr>
                            <#list api.requestInfo.requestParam.rowList as rowInfo>
                                <w:tr w14:paraId="6F5FBFA7">
                                    <w:tc>
                                        <w:tcPr>
                                            <w:tcW w:w="1704" w:type="dxa"/>
                                        </w:tcPr>
                                        <w:p w14:paraId="6F3DD574">
                                            <w:pPr>
                                                <w:widowControl w:val="0"/>
                                                <w:numPr>
                                                    <w:ilvl w:val="0"/>
                                                    <w:numId w:val="0"/>
                                                </w:numPr>
                                                <w:jc w:val="center"/>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                            </w:pPr>
                                            <w:r>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                                <w:t>${rowInfo.name!''}</w:t>
                                            </w:r>
                                        </w:p>
                                    </w:tc>
                                    <w:tc>
                                        <w:tcPr>
                                            <w:tcW w:w="1704" w:type="dxa"/>
                                        </w:tcPr>
                                        <w:p w14:paraId="1BB8882A">
                                            <w:pPr>
                                                <w:widowControl w:val="0"/>
                                                <w:numPr>
                                                    <w:ilvl w:val="0"/>
                                                    <w:numId w:val="0"/>
                                                </w:numPr>
                                                <w:jc w:val="center"/>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                            </w:pPr>
                                            <w:r>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                                <w:t>${rowInfo.type!''}</w:t>
                                            </w:r>
                                        </w:p>
                                    </w:tc>
                                    <w:tc>
                                        <w:tcPr>
                                            <w:tcW w:w="1704" w:type="dxa"/>
                                        </w:tcPr>
                                        <w:p w14:paraId="4BE94AD4">
                                            <w:pPr>
                                                <w:widowControl w:val="0"/>
                                                <w:numPr>
                                                    <w:ilvl w:val="0"/>
                                                    <w:numId w:val="0"/>
                                                </w:numPr>
                                                <w:jc w:val="center"/>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                            </w:pPr>
                                            <w:r>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                                <w:t>${rowInfo.required?then('Y', 'N')}</w:t>
                                            </w:r>
                                        </w:p>
                                    </w:tc>
                                    <w:tc>
                                        <w:tcPr>
                                            <w:tcW w:w="1705" w:type="dxa"/>
                                        </w:tcPr>
                                        <w:p w14:paraId="76BE87EA">
                                            <w:pPr>
                                                <w:widowControl w:val="0"/>
                                                <w:numPr>
                                                    <w:ilvl w:val="0"/>
                                                    <w:numId w:val="0"/>
                                                </w:numPr>
                                                <w:jc w:val="center"/>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                            </w:pPr>
                                            <w:r>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                                <w:t>${rowInfo.description!''}</w:t>
                                            </w:r>
                                        </w:p>
                                    </w:tc>
                                    <w:tc>
                                        <w:tcPr>
                                            <w:tcW w:w="1705" w:type="dxa"/>
                                        </w:tcPr>
                                        <w:p w14:paraId="5653E67C">
                                            <w:pPr>
                                                <w:widowControl w:val="0"/>
                                                <w:numPr>
                                                    <w:ilvl w:val="0"/>
                                                    <w:numId w:val="0"/>
                                                </w:numPr>
                                                <w:jc w:val="center"/>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                            </w:pPr>
                                            <w:r>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                                <w:t>${rowInfo.example!''}</w:t>
                                            </w:r>
                                        </w:p>
                                    </w:tc>
                                </w:tr>
                            </#list>
                        </w:tbl>
                    </#if>
                    <#if api.requestInfo.formParam?? && api.requestInfo.formParam.rowList?? && api.requestInfo.formParam.rowList?has_content>
                        <w:p w14:paraId="085A9937">
                            <w:pPr>
                                <w:pStyle w:val="5"/>
                                <w:keepNext/>
                                <w:keepLines/>
                                <w:pageBreakBefore w:val="0"/>
                                <w:widowControl w:val="0"/>
                                <w:kinsoku/>
                                <w:wordWrap/>
                                <w:overflowPunct/>
                                <w:topLinePunct w:val="0"/>
                                <w:autoSpaceDE/>
                                <w:autoSpaceDN/>
                                <w:bidi w:val="0"/>
                                <w:adjustRightInd/>
                                <w:snapToGrid/>
                                <w:spacing w:before="157" w:beforeLines="50" w:after="157" w:afterLines="50"
                                           w:line="360"
                                           w:lineRule="auto"/>
                                <w:textAlignment w:val="auto"/>
                                <w:rPr>
                                    <w:rFonts w:hint="eastAsia"/>
                                    <w:b w:val="0"/>
                                    <w:bCs/>
                                    <w:sz w:val="24"/>
                                    <w:szCs w:val="24"/>
                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                </w:rPr>
                            </w:pPr>
                            <w:r>
                                <w:rPr>
                                    <w:rFonts w:hint="eastAsia"/>
                                    <w:b w:val="0"/>
                                    <w:bCs/>
                                    <w:sz w:val="24"/>
                                    <w:szCs w:val="24"/>
                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                </w:rPr>
                                <w:t>表单参数（</w:t>
                            </w:r>
                            <w:r>
                                <w:rPr>
                                    <w:rFonts w:hint="default" w:ascii="Times New Roman Regular"
                                              w:hAnsi="Times New Roman Regular" w:cs="Times New Roman Regular"/>
                                    <w:b w:val="0"/>
                                    <w:bCs/>
                                    <w:sz w:val="24"/>
                                    <w:szCs w:val="24"/>
                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                </w:rPr>
                                <w:t>FormParam</w:t>
                            </w:r>
                            <w:r>
                                <w:rPr>
                                    <w:rFonts w:hint="eastAsia"/>
                                    <w:b w:val="0"/>
                                    <w:bCs/>
                                    <w:sz w:val="24"/>
                                    <w:szCs w:val="24"/>
                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                </w:rPr>
                                <w:t>）</w:t>
                            </w:r>
                        </w:p>
                        <w:tbl>
                            <w:tblPr>
                                <w:tblStyle w:val="8"/>
                                <w:tblW w:w="0" w:type="auto"/>
                                <w:tblInd w:w="0" w:type="dxa"/>
                                <w:tblBorders>
                                    <w:top w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                                    <w:left w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                                    <w:bottom w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                                    <w:right w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                                    <w:insideH w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                                    <w:insideV w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                                </w:tblBorders>
                                <w:tblLayout w:type="autofit"/>
                                <w:tblCellMar>
                                    <w:top w:w="0" w:type="dxa"/>
                                    <w:left w:w="108" w:type="dxa"/>
                                    <w:bottom w:w="0" w:type="dxa"/>
                                    <w:right w:w="108" w:type="dxa"/>
                                </w:tblCellMar>
                            </w:tblPr>
                            <w:tblGrid>
                                <w:gridCol w:w="1704"/>
                                <w:gridCol w:w="1704"/>
                                <w:gridCol w:w="1704"/>
                                <w:gridCol w:w="1705"/>
                                <w:gridCol w:w="1705"/>
                            </w:tblGrid>
                            <w:tr w14:paraId="6F7C4B1C">
                                <w:tc>
                                    <w:tcPr>
                                        <w:tcW w:w="1704" w:type="dxa"/>
                                        <w:shd w:val="clear" w:color="auto" w:fill="E7E6E6"/>
                                    </w:tcPr>
                                    <w:p w14:paraId="62FA523C">
                                        <w:pPr>
                                            <w:widowControl w:val="0"/>
                                            <w:numPr>
                                                <w:ilvl w:val="0"/>
                                                <w:numId w:val="0"/>
                                            </w:numPr>
                                            <w:jc w:val="center"/>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                        </w:pPr>
                                        <w:r>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                            <w:t>名称</w:t>
                                        </w:r>
                                    </w:p>
                                </w:tc>
                                <w:tc>
                                    <w:tcPr>
                                        <w:tcW w:w="1704" w:type="dxa"/>
                                        <w:shd w:val="clear" w:color="auto" w:fill="E7E6E6"/>
                                    </w:tcPr>
                                    <w:p w14:paraId="18AAFB99">
                                        <w:pPr>
                                            <w:widowControl w:val="0"/>
                                            <w:numPr>
                                                <w:ilvl w:val="0"/>
                                                <w:numId w:val="0"/>
                                            </w:numPr>
                                            <w:jc w:val="center"/>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                        </w:pPr>
                                        <w:r>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                            <w:t>类型</w:t>
                                        </w:r>
                                    </w:p>
                                </w:tc>
                                <w:tc>
                                    <w:tcPr>
                                        <w:tcW w:w="1704" w:type="dxa"/>
                                        <w:shd w:val="clear" w:color="auto" w:fill="E7E6E6"/>
                                    </w:tcPr>
                                    <w:p w14:paraId="786B8822">
                                        <w:pPr>
                                            <w:widowControl w:val="0"/>
                                            <w:numPr>
                                                <w:ilvl w:val="0"/>
                                                <w:numId w:val="0"/>
                                            </w:numPr>
                                            <w:jc w:val="center"/>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                        </w:pPr>
                                        <w:r>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                            <w:t>必填</w:t>
                                        </w:r>
                                    </w:p>
                                </w:tc>
                                <w:tc>
                                    <w:tcPr>
                                        <w:tcW w:w="1705" w:type="dxa"/>
                                        <w:shd w:val="clear" w:color="auto" w:fill="E7E6E6"/>
                                    </w:tcPr>
                                    <w:p w14:paraId="1F45921C">
                                        <w:pPr>
                                            <w:widowControl w:val="0"/>
                                            <w:numPr>
                                                <w:ilvl w:val="0"/>
                                                <w:numId w:val="0"/>
                                            </w:numPr>
                                            <w:jc w:val="center"/>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                        </w:pPr>
                                        <w:r>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                            <w:t>说明</w:t>
                                        </w:r>
                                    </w:p>
                                </w:tc>
                                <w:tc>
                                    <w:tcPr>
                                        <w:tcW w:w="1705" w:type="dxa"/>
                                        <w:shd w:val="clear" w:color="auto" w:fill="E7E6E6"/>
                                    </w:tcPr>
                                    <w:p w14:paraId="05BE14D6">
                                        <w:pPr>
                                            <w:widowControl w:val="0"/>
                                            <w:numPr>
                                                <w:ilvl w:val="0"/>
                                                <w:numId w:val="0"/>
                                            </w:numPr>
                                            <w:jc w:val="center"/>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                        </w:pPr>
                                        <w:r>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                            <w:t>示例</w:t>
                                        </w:r>
                                    </w:p>
                                </w:tc>
                            </w:tr>
                            <#list api.requestInfo.formParam.rowList as rowInfo>
                                <w:tr w14:paraId="48831F71">
                                    <w:tc>
                                        <w:tcPr>
                                            <w:tcW w:w="1704" w:type="dxa"/>
                                        </w:tcPr>
                                        <w:p w14:paraId="09BD8515">
                                            <w:pPr>
                                                <w:widowControl w:val="0"/>
                                                <w:numPr>
                                                    <w:ilvl w:val="0"/>
                                                    <w:numId w:val="0"/>
                                                </w:numPr>
                                                <w:jc w:val="center"/>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                            </w:pPr>
                                            <w:r>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                                <w:t>${rowInfo.name!''}</w:t>
                                            </w:r>
                                        </w:p>
                                    </w:tc>
                                    <w:tc>
                                        <w:tcPr>
                                            <w:tcW w:w="1704" w:type="dxa"/>
                                        </w:tcPr>
                                        <w:p w14:paraId="4930EBDB">
                                            <w:pPr>
                                                <w:widowControl w:val="0"/>
                                                <w:numPr>
                                                    <w:ilvl w:val="0"/>
                                                    <w:numId w:val="0"/>
                                                </w:numPr>
                                                <w:jc w:val="center"/>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                            </w:pPr>
                                            <w:r>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                                <w:t>${rowInfo.type!''}</w:t>
                                            </w:r>
                                        </w:p>
                                    </w:tc>
                                    <w:tc>
                                        <w:tcPr>
                                            <w:tcW w:w="1704" w:type="dxa"/>
                                        </w:tcPr>
                                        <w:p w14:paraId="7D33C258">
                                            <w:pPr>
                                                <w:widowControl w:val="0"/>
                                                <w:numPr>
                                                    <w:ilvl w:val="0"/>
                                                    <w:numId w:val="0"/>
                                                </w:numPr>
                                                <w:jc w:val="center"/>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                            </w:pPr>
                                            <w:r>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                                <w:t>${rowInfo.required?then('Y', 'N')}</w:t>
                                            </w:r>
                                        </w:p>
                                    </w:tc>
                                    <w:tc>
                                        <w:tcPr>
                                            <w:tcW w:w="1705" w:type="dxa"/>
                                        </w:tcPr>
                                        <w:p w14:paraId="0729F022">
                                            <w:pPr>
                                                <w:widowControl w:val="0"/>
                                                <w:numPr>
                                                    <w:ilvl w:val="0"/>
                                                    <w:numId w:val="0"/>
                                                </w:numPr>
                                                <w:jc w:val="center"/>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                            </w:pPr>
                                            <w:r>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                                <w:t>${rowInfo.description!''}</w:t>
                                            </w:r>
                                        </w:p>
                                    </w:tc>
                                    <w:tc>
                                        <w:tcPr>
                                            <w:tcW w:w="1705" w:type="dxa"/>
                                        </w:tcPr>
                                        <w:p w14:paraId="669FA0EE">
                                            <w:pPr>
                                                <w:widowControl w:val="0"/>
                                                <w:numPr>
                                                    <w:ilvl w:val="0"/>
                                                    <w:numId w:val="0"/>
                                                </w:numPr>
                                                <w:jc w:val="center"/>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                            </w:pPr>
                                            <w:r>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                                <w:t>${rowInfo.example!''}</w:t>
                                            </w:r>
                                        </w:p>
                                    </w:tc>
                                </w:tr>
                            </#list>
                        </w:tbl>
                    </#if>
                    <#if api.requestInfo.requestBody?? && api.requestInfo.requestBody.rowList?? && api.requestInfo.requestBody.rowList?has_content>
                        <w:p w14:paraId="63374106">
                            <w:pPr>
                                <w:pStyle w:val="5"/>
                                <w:keepNext/>
                                <w:keepLines/>
                                <w:pageBreakBefore w:val="0"/>
                                <w:widowControl w:val="0"/>
                                <w:kinsoku/>
                                <w:wordWrap/>
                                <w:overflowPunct/>
                                <w:topLinePunct w:val="0"/>
                                <w:autoSpaceDE/>
                                <w:autoSpaceDN/>
                                <w:bidi w:val="0"/>
                                <w:adjustRightInd/>
                                <w:snapToGrid/>
                                <w:spacing w:before="157" w:beforeLines="50" w:after="157" w:afterLines="50"
                                           w:line="360"
                                           w:lineRule="auto"/>
                                <w:textAlignment w:val="auto"/>
                                <w:rPr>
                                    <w:rFonts w:hint="eastAsia"/>
                                    <w:sz w:val="24"/>
                                    <w:szCs w:val="24"/>
                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                </w:rPr>
                            </w:pPr>
                            <w:r>
                                <w:rPr>
                                    <w:rFonts w:hint="eastAsia"/>
                                    <w:b w:val="0"/>
                                    <w:bCs/>
                                    <w:sz w:val="24"/>
                                    <w:szCs w:val="24"/>
                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                </w:rPr>
                                <w:t>请求体参数（</w:t>
                            </w:r>
                            <w:r>
                                <w:rPr>
                                    <w:rFonts w:hint="default" w:ascii="Times New Roman Regular"
                                              w:hAnsi="Times New Roman Regular" w:cs="Times New Roman Regular"/>
                                    <w:b w:val="0"/>
                                    <w:bCs/>
                                    <w:sz w:val="24"/>
                                    <w:szCs w:val="24"/>
                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                </w:rPr>
                                <w:t>RequestBody</w:t>
                            </w:r>
                            <w:r>
                                <w:rPr>
                                    <w:rFonts w:hint="eastAsia"/>
                                    <w:b w:val="0"/>
                                    <w:bCs/>
                                    <w:sz w:val="24"/>
                                    <w:szCs w:val="24"/>
                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                </w:rPr>
                                <w:t>）</w:t>
                            </w:r>
                        </w:p>
                        <w:p w14:paraId="4A32EB9F">
                            <w:pPr>
                                <w:keepNext w:val="0"/>
                                <w:keepLines w:val="0"/>
                                <w:pageBreakBefore w:val="0"/>
                                <w:widowControl w:val="0"/>
                                <w:numPr>
                                    <w:numId w:val="0"/>
                                </w:numPr>
                                <w:kinsoku/>
                                <w:wordWrap/>
                                <w:overflowPunct/>
                                <w:topLinePunct w:val="0"/>
                                <w:autoSpaceDE/>
                                <w:autoSpaceDN/>
                                <w:bidi w:val="0"/>
                                <w:adjustRightInd/>
                                <w:snapToGrid/>
                                <w:spacing w:before="157" w:beforeLines="50" w:after="157" w:afterLines="50"
                                           w:line="240"
                                           w:lineRule="auto"/>
                                <w:jc w:val="both"/>
                                <w:textAlignment w:val="auto"/>
                                <w:rPr>
                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体" w:eastAsia="宋体"
                                              w:cs="宋体"/>
                                    <w:b/>
                                    <w:bCs/>
                                    <w:sz w:val="21"/>
                                    <w:szCs w:val="21"/>
                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                </w:rPr>
                            </w:pPr>
                            <w:r>
                                <w:rPr>
                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体" w:eastAsia="宋体"
                                              w:cs="宋体"/>
                                    <w:b/>
                                    <w:bCs/>
                                    <w:sz w:val="21"/>
                                    <w:szCs w:val="21"/>
                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                </w:rPr>
                                <w:t>描述</w:t>
                            </w:r>
                        </w:p>
                        <w:tbl>
                            <w:tblPr>
                                <w:tblStyle w:val="8"/>
                                <w:tblW w:w="0" w:type="auto"/>
                                <w:tblInd w:w="0" w:type="dxa"/>
                                <w:tblBorders>
                                    <w:top w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                                    <w:left w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                                    <w:bottom w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                                    <w:right w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                                    <w:insideH w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                                    <w:insideV w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                                </w:tblBorders>
                                <w:tblLayout w:type="autofit"/>
                                <w:tblCellMar>
                                    <w:top w:w="0" w:type="dxa"/>
                                    <w:left w:w="108" w:type="dxa"/>
                                    <w:bottom w:w="0" w:type="dxa"/>
                                    <w:right w:w="108" w:type="dxa"/>
                                </w:tblCellMar>
                            </w:tblPr>
                            <w:tblGrid>
                                <w:gridCol w:w="1704"/>
                                <w:gridCol w:w="1704"/>
                                <w:gridCol w:w="1704"/>
                                <w:gridCol w:w="1705"/>
                                <w:gridCol w:w="1705"/>
                            </w:tblGrid>
                            <w:tr w14:paraId="11C41418">
                                <w:tc>
                                    <w:tcPr>
                                        <w:tcW w:w="1704" w:type="dxa"/>
                                        <w:shd w:val="clear" w:color="auto" w:fill="E7E6E6"/>
                                    </w:tcPr>
                                    <w:p w14:paraId="71E042AC">
                                        <w:pPr>
                                            <w:widowControl w:val="0"/>
                                            <w:numPr>
                                                <w:ilvl w:val="0"/>
                                                <w:numId w:val="0"/>
                                            </w:numPr>
                                            <w:jc w:val="center"/>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                        </w:pPr>
                                        <w:r>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                            <w:t>名称</w:t>
                                        </w:r>
                                    </w:p>
                                </w:tc>
                                <w:tc>
                                    <w:tcPr>
                                        <w:tcW w:w="1704" w:type="dxa"/>
                                        <w:shd w:val="clear" w:color="auto" w:fill="E7E6E6"/>
                                    </w:tcPr>
                                    <w:p w14:paraId="47F361B8">
                                        <w:pPr>
                                            <w:widowControl w:val="0"/>
                                            <w:numPr>
                                                <w:ilvl w:val="0"/>
                                                <w:numId w:val="0"/>
                                            </w:numPr>
                                            <w:jc w:val="center"/>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                        </w:pPr>
                                        <w:r>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                            <w:t>类型</w:t>
                                        </w:r>
                                    </w:p>
                                </w:tc>
                                <w:tc>
                                    <w:tcPr>
                                        <w:tcW w:w="1704" w:type="dxa"/>
                                        <w:shd w:val="clear" w:color="auto" w:fill="E7E6E6"/>
                                    </w:tcPr>
                                    <w:p w14:paraId="1F2B3C4D">
                                        <w:pPr>
                                            <w:widowControl w:val="0"/>
                                            <w:numPr>
                                                <w:ilvl w:val="0"/>
                                                <w:numId w:val="0"/>
                                            </w:numPr>
                                            <w:jc w:val="center"/>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                        </w:pPr>
                                        <w:r>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                            <w:t>必填</w:t>
                                        </w:r>
                                    </w:p>
                                </w:tc>
                                <w:tc>
                                    <w:tcPr>
                                        <w:tcW w:w="1705" w:type="dxa"/>
                                        <w:shd w:val="clear" w:color="auto" w:fill="E7E6E6"/>
                                    </w:tcPr>
                                    <w:p w14:paraId="6DD3EA50">
                                        <w:pPr>
                                            <w:widowControl w:val="0"/>
                                            <w:numPr>
                                                <w:ilvl w:val="0"/>
                                                <w:numId w:val="0"/>
                                            </w:numPr>
                                            <w:jc w:val="center"/>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                        </w:pPr>
                                        <w:r>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                            <w:t>说明</w:t>
                                        </w:r>
                                    </w:p>
                                </w:tc>
                                <w:tc>
                                    <w:tcPr>
                                        <w:tcW w:w="1705" w:type="dxa"/>
                                        <w:shd w:val="clear" w:color="auto" w:fill="E7E6E6"/>
                                    </w:tcPr>
                                    <w:p w14:paraId="17AFA325">
                                        <w:pPr>
                                            <w:widowControl w:val="0"/>
                                            <w:numPr>
                                                <w:ilvl w:val="0"/>
                                                <w:numId w:val="0"/>
                                            </w:numPr>
                                            <w:jc w:val="center"/>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                        </w:pPr>
                                        <w:r>
                                            <w:rPr>
                                                <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                          w:eastAsia="宋体"
                                                          w:cs="宋体"/>
                                                <w:b/>
                                                <w:bCs/>
                                                <w:sz w:val="18"/>
                                                <w:szCs w:val="18"/>
                                                <w:vertAlign w:val="baseline"/>
                                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                            </w:rPr>
                                            <w:t>示例</w:t>
                                        </w:r>
                                    </w:p>
                                </w:tc>
                            </w:tr>
                            <#list api.requestInfo.requestBody.rowList as rowInfo>
                                <w:tr w14:paraId="3307E211">
                                    <w:tc>
                                        <w:tcPr>
                                            <w:tcW w:w="1704" w:type="dxa"/>
                                        </w:tcPr>
                                        <w:p w14:paraId="67771200">
                                            <w:pPr>
                                                <w:widowControl w:val="0"/>
                                                <w:numPr>
                                                    <w:ilvl w:val="0"/>
                                                    <w:numId w:val="0"/>
                                                </w:numPr>
                                                <w:jc w:val="left"/>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                            </w:pPr>
                                            <w:r>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                                <w:t>${rowInfo.name!''}</w:t>
                                            </w:r>
                                        </w:p>
                                    </w:tc>
                                    <w:tc>
                                        <w:tcPr>
                                            <w:tcW w:w="1704" w:type="dxa"/>
                                        </w:tcPr>
                                        <w:p w14:paraId="2632CF47">
                                            <w:pPr>
                                                <w:widowControl w:val="0"/>
                                                <w:numPr>
                                                    <w:ilvl w:val="0"/>
                                                    <w:numId w:val="0"/>
                                                </w:numPr>
                                                <w:jc w:val="left"/>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                            </w:pPr>
                                            <w:r>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                                <w:t>${rowInfo.type!''}</w:t>
                                            </w:r>
                                        </w:p>
                                    </w:tc>
                                    <w:tc>
                                        <w:tcPr>
                                            <w:tcW w:w="1704" w:type="dxa"/>
                                        </w:tcPr>
                                        <w:p w14:paraId="30780A9A">
                                            <w:pPr>
                                                <w:widowControl w:val="0"/>
                                                <w:numPr>
                                                    <w:ilvl w:val="0"/>
                                                    <w:numId w:val="0"/>
                                                </w:numPr>
                                                <w:jc w:val="left"/>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                            </w:pPr>
                                            <w:r>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                                <w:t>${rowInfo.required?then('Y', 'N')}</w:t>
                                            </w:r>
                                        </w:p>
                                    </w:tc>
                                    <w:tc>
                                        <w:tcPr>
                                            <w:tcW w:w="1705" w:type="dxa"/>
                                        </w:tcPr>
                                        <w:p w14:paraId="4AEACF25">
                                            <w:pPr>
                                                <w:widowControl w:val="0"/>
                                                <w:numPr>
                                                    <w:ilvl w:val="0"/>
                                                    <w:numId w:val="0"/>
                                                </w:numPr>
                                                <w:jc w:val="left"/>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                            </w:pPr>
                                            <w:r>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                                <w:t>${rowInfo.description!''}</w:t>
                                            </w:r>
                                        </w:p>
                                    </w:tc>
                                    <w:tc>
                                        <w:tcPr>
                                            <w:tcW w:w="1705" w:type="dxa"/>
                                        </w:tcPr>
                                        <w:p w14:paraId="1D176D21">
                                            <w:pPr>
                                                <w:widowControl w:val="0"/>
                                                <w:numPr>
                                                    <w:ilvl w:val="0"/>
                                                    <w:numId w:val="0"/>
                                                </w:numPr>
                                                <w:jc w:val="left"/>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                            </w:pPr>
                                            <w:r>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b w:val="0"/>
                                                    <w:bCs w:val="0"/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                                <w:t>${rowInfo.example!''}</w:t>
                                            </w:r>
                                        </w:p>
                                    </w:tc>
                                </w:tr>
                            </#list>
                        </w:tbl>
                        <w:p w14:paraId="15251F48">
                            <w:pPr>
                                <w:keepNext w:val="0"/>
                                <w:keepLines w:val="0"/>
                                <w:pageBreakBefore w:val="0"/>
                                <w:widowControl w:val="0"/>
                                <w:numPr>
                                    <w:numId w:val="0"/>
                                </w:numPr>
                                <w:kinsoku/>
                                <w:wordWrap/>
                                <w:overflowPunct/>
                                <w:topLinePunct w:val="0"/>
                                <w:autoSpaceDE/>
                                <w:autoSpaceDN/>
                                <w:bidi w:val="0"/>
                                <w:adjustRightInd/>
                                <w:snapToGrid/>
                                <w:spacing w:before="157" w:beforeLines="50" w:after="157" w:afterLines="50"
                                           w:line="240"
                                           w:lineRule="auto"/>
                                <w:jc w:val="both"/>
                                <w:textAlignment w:val="auto"/>
                                <w:rPr>
                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体" w:eastAsia="宋体"
                                              w:cs="宋体"/>
                                    <w:b/>
                                    <w:bCs/>
                                    <w:sz w:val="21"/>
                                    <w:szCs w:val="21"/>
                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                </w:rPr>
                            </w:pPr>
                            <w:r>
                                <w:rPr>
                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体" w:eastAsia="宋体"
                                              w:cs="宋体"/>
                                    <w:b/>
                                    <w:bCs/>
                                    <w:sz w:val="21"/>
                                    <w:szCs w:val="21"/>
                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                </w:rPr>
                                <w:t>示例</w:t>
                            </w:r>
                        </w:p>
                        ${api.requestInfo.requestBodyJson!''}
                    </#if>
                    <#else>
                        <w:p w14:paraId="06517EF0">
                            <w:pPr>
                                <w:widowControl w:val="0"/>
                                <w:numPr>
                                    <w:numId w:val="0"/>
                                </w:numPr>
                                <w:jc w:val="both"/>
                                <w:rPr>
                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体" w:eastAsia="宋体"
                                              w:cs="宋体"/>
                                    <w:b w:val="0"/>
                                    <w:bCs/>
                                    <w:sz w:val="18"/>
                                    <w:szCs w:val="18"/>
                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                </w:rPr>
                            </w:pPr>
                            <w:r>
                                <w:rPr>
                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体" w:eastAsia="宋体"
                                              w:cs="宋体"/>
                                    <w:b w:val="0"/>
                                    <w:bCs/>
                                    <w:sz w:val="18"/>
                                    <w:szCs w:val="18"/>
                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                </w:rPr>
                                <w:t>无请求参数</w:t>
                            </w:r>
                        </w:p>
                    </#if>
                    <w:p w14:paraId="004E1DBF">
                        <w:pPr>
                            <w:pStyle w:val="4"/>
                            <w:keepNext/>
                            <w:keepLines/>
                            <w:pageBreakBefore w:val="0"/>
                            <w:widowControl w:val="0"/>
                            <w:kinsoku/>
                            <w:wordWrap/>
                            <w:overflowPunct/>
                            <w:topLinePunct w:val="0"/>
                            <w:autoSpaceDE/>
                            <w:autoSpaceDN/>
                            <w:bidi w:val="0"/>
                            <w:adjustRightInd/>
                            <w:snapToGrid/>
                            <w:spacing w:before="157" w:beforeLines="50" w:after="157" w:afterLines="50" w:line="360"
                                       w:lineRule="auto"/>
                            <w:textAlignment w:val="auto"/>
                            <w:rPr>
                                <w:rFonts w:hint="eastAsia" w:ascii="黑体" w:hAnsi="黑体" w:eastAsia="黑体"
                                          w:cs="黑体"/>
                                <w:b w:val="0"/>
                                <w:bCs/>
                                <w:sz w:val="28"/>
                                <w:szCs w:val="28"/>
                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                            </w:rPr>
                        </w:pPr>
                        <w:r>
                            <w:rPr>
                                <w:rFonts w:hint="eastAsia" w:ascii="黑体" w:hAnsi="黑体" w:eastAsia="黑体"
                                          w:cs="黑体"/>
                                <w:b w:val="0"/>
                                <w:bCs/>
                                <w:sz w:val="28"/>
                                <w:szCs w:val="28"/>
                                <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                            </w:rPr>
                            <w:t>响应参数</w:t>
                        </w:r>
                    </w:p>
                    <#if api.responseInfo??>
                        <#if api.responseInfo.responseBody?? && api.responseInfo.responseBody.rowList?? && api.responseInfo.responseBody.rowList?has_content>
                            <w:p w14:paraId="50C27D4A">
                                <w:pPr>
                                    <w:pStyle w:val="5"/>
                                    <w:keepNext/>
                                    <w:keepLines/>
                                    <w:pageBreakBefore w:val="0"/>
                                    <w:widowControl w:val="0"/>
                                    <w:kinsoku/>
                                    <w:wordWrap/>
                                    <w:overflowPunct/>
                                    <w:topLinePunct w:val="0"/>
                                    <w:autoSpaceDE/>
                                    <w:autoSpaceDN/>
                                    <w:bidi w:val="0"/>
                                    <w:adjustRightInd/>
                                    <w:snapToGrid/>
                                    <w:spacing w:before="157" w:beforeLines="50" w:after="157" w:afterLines="50"
                                               w:line="360"
                                               w:lineRule="auto"/>
                                    <w:textAlignment w:val="auto"/>
                                    <w:rPr>
                                        <w:rFonts w:hint="eastAsia"/>
                                        <w:sz w:val="24"/>
                                        <w:szCs w:val="24"/>
                                        <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                    </w:rPr>
                                </w:pPr>
                                <w:r>
                                    <w:rPr>
                                        <w:rFonts w:hint="eastAsia"/>
                                        <w:b w:val="0"/>
                                        <w:bCs/>
                                        <w:sz w:val="24"/>
                                        <w:szCs w:val="24"/>
                                        <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                    </w:rPr>
                                    <w:t>响应体参数（</w:t>
                                </w:r>
                                <w:r>
                                    <w:rPr>
                                        <w:rFonts w:hint="default" w:ascii="Times New Roman Regular"
                                                  w:hAnsi="Times New Roman Regular" w:cs="Times New Roman Regular"/>
                                        <w:b w:val="0"/>
                                        <w:bCs/>
                                        <w:sz w:val="24"/>
                                        <w:szCs w:val="24"/>
                                        <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                    </w:rPr>
                                    <w:t>RequestBody</w:t>
                                </w:r>
                                <w:r>
                                    <w:rPr>
                                        <w:rFonts w:hint="eastAsia"/>
                                        <w:b w:val="0"/>
                                        <w:bCs/>
                                        <w:sz w:val="24"/>
                                        <w:szCs w:val="24"/>
                                        <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                    </w:rPr>
                                    <w:t>）</w:t>
                                </w:r>
                            </w:p>
                            <w:p w14:paraId="4B1666FE">
                                <w:pPr>
                                    <w:keepNext w:val="0"/>
                                    <w:keepLines w:val="0"/>
                                    <w:pageBreakBefore w:val="0"/>
                                    <w:widowControl w:val="0"/>
                                    <w:numPr>
                                        <w:ilvl w:val="0"/>
                                        <w:numId w:val="0"/>
                                    </w:numPr>
                                    <w:kinsoku/>
                                    <w:wordWrap/>
                                    <w:overflowPunct/>
                                    <w:topLinePunct w:val="0"/>
                                    <w:autoSpaceDE/>
                                    <w:autoSpaceDN/>
                                    <w:bidi w:val="0"/>
                                    <w:adjustRightInd/>
                                    <w:snapToGrid/>
                                    <w:spacing w:before="157" w:beforeLines="50" w:after="157" w:afterLines="50"
                                               w:line="240"
                                               w:lineRule="auto"/>
                                    <w:jc w:val="both"/>
                                    <w:textAlignment w:val="auto"/>
                                    <w:rPr>
                                        <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体" w:eastAsia="宋体"
                                                  w:cs="宋体"/>
                                        <w:b/>
                                        <w:bCs/>
                                        <w:sz w:val="21"/>
                                        <w:szCs w:val="21"/>
                                        <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                    </w:rPr>
                                </w:pPr>
                                <w:r>
                                    <w:rPr>
                                        <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体" w:eastAsia="宋体"
                                                  w:cs="宋体"/>
                                        <w:b/>
                                        <w:bCs/>
                                        <w:sz w:val="21"/>
                                        <w:szCs w:val="21"/>
                                        <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                    </w:rPr>
                                    <w:t>描述</w:t>
                                </w:r>
                            </w:p>
                            <w:tbl>
                                <w:tblPr>
                                    <w:tblStyle w:val="8"/>
                                    <w:tblW w:w="0" w:type="auto"/>
                                    <w:tblInd w:w="0" w:type="dxa"/>
                                    <w:tblBorders>
                                        <w:top w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                                        <w:left w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                                        <w:bottom w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                                        <w:right w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                                        <w:insideH w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                                        <w:insideV w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                                    </w:tblBorders>
                                    <w:tblLayout w:type="autofit"/>
                                    <w:tblCellMar>
                                        <w:top w:w="0" w:type="dxa"/>
                                        <w:left w:w="108" w:type="dxa"/>
                                        <w:bottom w:w="0" w:type="dxa"/>
                                        <w:right w:w="108" w:type="dxa"/>
                                    </w:tblCellMar>
                                </w:tblPr>
                                <w:tblGrid>
                                    <w:gridCol w:w="1704"/>
                                    <w:gridCol w:w="1704"/>
                                    <w:gridCol w:w="1704"/>
                                    <w:gridCol w:w="1705"/>
                                    <w:gridCol w:w="1705"/>
                                </w:tblGrid>
                                <w:tr w14:paraId="40ECB5A7">
                                    <w:tc>
                                        <w:tcPr>
                                            <w:tcW w:w="1704" w:type="dxa"/>
                                            <w:shd w:val="clear" w:color="auto" w:fill="E7E6E6"/>
                                        </w:tcPr>
                                        <w:p w14:paraId="0DC580F4">
                                            <w:pPr>
                                                <w:widowControl w:val="0"/>
                                                <w:numPr>
                                                    <w:ilvl w:val="0"/>
                                                    <w:numId w:val="0"/>
                                                </w:numPr>
                                                <w:jc w:val="center"/>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b/>
                                                    <w:bCs/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                            </w:pPr>
                                            <w:r>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b/>
                                                    <w:bCs/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                                <w:t>名称</w:t>
                                            </w:r>
                                        </w:p>
                                    </w:tc>
                                    <w:tc>
                                        <w:tcPr>
                                            <w:tcW w:w="1704" w:type="dxa"/>
                                            <w:shd w:val="clear" w:color="auto" w:fill="E7E6E6"/>
                                        </w:tcPr>
                                        <w:p w14:paraId="26DF4889">
                                            <w:pPr>
                                                <w:widowControl w:val="0"/>
                                                <w:numPr>
                                                    <w:ilvl w:val="0"/>
                                                    <w:numId w:val="0"/>
                                                </w:numPr>
                                                <w:jc w:val="center"/>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b/>
                                                    <w:bCs/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                            </w:pPr>
                                            <w:r>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b/>
                                                    <w:bCs/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                                <w:t>类型</w:t>
                                            </w:r>
                                        </w:p>
                                    </w:tc>
                                    <w:tc>
                                        <w:tcPr>
                                            <w:tcW w:w="1704" w:type="dxa"/>
                                            <w:shd w:val="clear" w:color="auto" w:fill="E7E6E6"/>
                                        </w:tcPr>
                                        <w:p w14:paraId="10400032">
                                            <w:pPr>
                                                <w:widowControl w:val="0"/>
                                                <w:numPr>
                                                    <w:ilvl w:val="0"/>
                                                    <w:numId w:val="0"/>
                                                </w:numPr>
                                                <w:jc w:val="center"/>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b/>
                                                    <w:bCs/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                            </w:pPr>
                                            <w:r>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b/>
                                                    <w:bCs/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                                <w:t>必填</w:t>
                                            </w:r>
                                        </w:p>
                                    </w:tc>
                                    <w:tc>
                                        <w:tcPr>
                                            <w:tcW w:w="1705" w:type="dxa"/>
                                            <w:shd w:val="clear" w:color="auto" w:fill="E7E6E6"/>
                                        </w:tcPr>
                                        <w:p w14:paraId="0C6CA8B9">
                                            <w:pPr>
                                                <w:widowControl w:val="0"/>
                                                <w:numPr>
                                                    <w:ilvl w:val="0"/>
                                                    <w:numId w:val="0"/>
                                                </w:numPr>
                                                <w:jc w:val="center"/>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b/>
                                                    <w:bCs/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                            </w:pPr>
                                            <w:r>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b/>
                                                    <w:bCs/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                                <w:t>说明</w:t>
                                            </w:r>
                                        </w:p>
                                    </w:tc>
                                    <w:tc>
                                        <w:tcPr>
                                            <w:tcW w:w="1705" w:type="dxa"/>
                                            <w:shd w:val="clear" w:color="auto" w:fill="E7E6E6"/>
                                        </w:tcPr>
                                        <w:p w14:paraId="56DE357E">
                                            <w:pPr>
                                                <w:widowControl w:val="0"/>
                                                <w:numPr>
                                                    <w:ilvl w:val="0"/>
                                                    <w:numId w:val="0"/>
                                                </w:numPr>
                                                <w:jc w:val="center"/>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b/>
                                                    <w:bCs/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                            </w:pPr>
                                            <w:r>
                                                <w:rPr>
                                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                              w:eastAsia="宋体"
                                                              w:cs="宋体"/>
                                                    <w:b/>
                                                    <w:bCs/>
                                                    <w:sz w:val="18"/>
                                                    <w:szCs w:val="18"/>
                                                    <w:vertAlign w:val="baseline"/>
                                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                </w:rPr>
                                                <w:t>示例</w:t>
                                            </w:r>
                                        </w:p>
                                    </w:tc>
                                </w:tr>
                                <#list api.responseInfo.responseBody.rowList as rowInfo>
                                    <w:tr w14:paraId="7A8E0889">
                                        <w:tc>
                                            <w:tcPr>
                                                <w:tcW w:w="1704" w:type="dxa"/>
                                            </w:tcPr>
                                            <w:p w14:paraId="030BED02">
                                                <w:pPr>
                                                    <w:widowControl w:val="0"/>
                                                    <w:numPr>
                                                        <w:ilvl w:val="0"/>
                                                        <w:numId w:val="0"/>
                                                    </w:numPr>
                                                    <w:jc w:val="left"/>
                                                    <w:rPr>
                                                        <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                                  w:eastAsia="宋体"
                                                                  w:cs="宋体"/>
                                                        <w:b w:val="0"/>
                                                        <w:bCs w:val="0"/>
                                                        <w:sz w:val="18"/>
                                                        <w:szCs w:val="18"/>
                                                        <w:vertAlign w:val="baseline"/>
                                                        <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                    </w:rPr>
                                                </w:pPr>
                                                <w:r>
                                                    <w:rPr>
                                                        <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                                  w:eastAsia="宋体"
                                                                  w:cs="宋体"/>
                                                        <w:b w:val="0"/>
                                                        <w:bCs w:val="0"/>
                                                        <w:sz w:val="18"/>
                                                        <w:szCs w:val="18"/>
                                                        <w:vertAlign w:val="baseline"/>
                                                        <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                    </w:rPr>
                                                    <w:t>${rowInfo.name!''}</w:t>
                                                </w:r>
                                            </w:p>
                                        </w:tc>
                                        <w:tc>
                                            <w:tcPr>
                                                <w:tcW w:w="1704" w:type="dxa"/>
                                            </w:tcPr>
                                            <w:p w14:paraId="4815181E">
                                                <w:pPr>
                                                    <w:widowControl w:val="0"/>
                                                    <w:numPr>
                                                        <w:ilvl w:val="0"/>
                                                        <w:numId w:val="0"/>
                                                    </w:numPr>
                                                    <w:jc w:val="left"/>
                                                    <w:rPr>
                                                        <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                                  w:eastAsia="宋体"
                                                                  w:cs="宋体"/>
                                                        <w:b w:val="0"/>
                                                        <w:bCs w:val="0"/>
                                                        <w:sz w:val="18"/>
                                                        <w:szCs w:val="18"/>
                                                        <w:vertAlign w:val="baseline"/>
                                                        <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                    </w:rPr>
                                                </w:pPr>
                                                <w:r>
                                                    <w:rPr>
                                                        <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                                  w:eastAsia="宋体"
                                                                  w:cs="宋体"/>
                                                        <w:b w:val="0"/>
                                                        <w:bCs w:val="0"/>
                                                        <w:sz w:val="18"/>
                                                        <w:szCs w:val="18"/>
                                                        <w:vertAlign w:val="baseline"/>
                                                        <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                    </w:rPr>
                                                    <w:t>${rowInfo.type!''}</w:t>
                                                </w:r>
                                            </w:p>
                                        </w:tc>
                                        <w:tc>
                                            <w:tcPr>
                                                <w:tcW w:w="1704" w:type="dxa"/>
                                            </w:tcPr>
                                            <w:p w14:paraId="5E1361BE">
                                                <w:pPr>
                                                    <w:widowControl w:val="0"/>
                                                    <w:numPr>
                                                        <w:ilvl w:val="0"/>
                                                        <w:numId w:val="0"/>
                                                    </w:numPr>
                                                    <w:jc w:val="left"/>
                                                    <w:rPr>
                                                        <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                                  w:eastAsia="宋体"
                                                                  w:cs="宋体"/>
                                                        <w:b w:val="0"/>
                                                        <w:bCs w:val="0"/>
                                                        <w:sz w:val="18"/>
                                                        <w:szCs w:val="18"/>
                                                        <w:vertAlign w:val="baseline"/>
                                                        <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                    </w:rPr>
                                                </w:pPr>
                                                <w:r>
                                                    <w:rPr>
                                                        <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                                  w:eastAsia="宋体"
                                                                  w:cs="宋体"/>
                                                        <w:b w:val="0"/>
                                                        <w:bCs w:val="0"/>
                                                        <w:sz w:val="18"/>
                                                        <w:szCs w:val="18"/>
                                                        <w:vertAlign w:val="baseline"/>
                                                        <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                    </w:rPr>
                                                    <w:t>${rowInfo.required?then('Y', 'N')}</w:t>
                                                </w:r>
                                            </w:p>
                                        </w:tc>
                                        <w:tc>
                                            <w:tcPr>
                                                <w:tcW w:w="1705" w:type="dxa"/>
                                            </w:tcPr>
                                            <w:p w14:paraId="3AA4D875">
                                                <w:pPr>
                                                    <w:widowControl w:val="0"/>
                                                    <w:numPr>
                                                        <w:ilvl w:val="0"/>
                                                        <w:numId w:val="0"/>
                                                    </w:numPr>
                                                    <w:jc w:val="left"/>
                                                    <w:rPr>
                                                        <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                                  w:eastAsia="宋体"
                                                                  w:cs="宋体"/>
                                                        <w:b w:val="0"/>
                                                        <w:bCs w:val="0"/>
                                                        <w:sz w:val="18"/>
                                                        <w:szCs w:val="18"/>
                                                        <w:vertAlign w:val="baseline"/>
                                                        <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                    </w:rPr>
                                                </w:pPr>
                                                <w:r>
                                                    <w:rPr>
                                                        <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                                  w:eastAsia="宋体"
                                                                  w:cs="宋体"/>
                                                        <w:b w:val="0"/>
                                                        <w:bCs w:val="0"/>
                                                        <w:sz w:val="18"/>
                                                        <w:szCs w:val="18"/>
                                                        <w:vertAlign w:val="baseline"/>
                                                        <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                    </w:rPr>
                                                    <w:t>${rowInfo.description!''}</w:t>
                                                </w:r>
                                            </w:p>
                                        </w:tc>
                                        <w:tc>
                                            <w:tcPr>
                                                <w:tcW w:w="1705" w:type="dxa"/>
                                            </w:tcPr>
                                            <w:p w14:paraId="71A95E18">
                                                <w:pPr>
                                                    <w:widowControl w:val="0"/>
                                                    <w:numPr>
                                                        <w:ilvl w:val="0"/>
                                                        <w:numId w:val="0"/>
                                                    </w:numPr>
                                                    <w:jc w:val="left"/>
                                                    <w:rPr>
                                                        <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                                  w:eastAsia="宋体"
                                                                  w:cs="宋体"/>
                                                        <w:b w:val="0"/>
                                                        <w:bCs w:val="0"/>
                                                        <w:sz w:val="18"/>
                                                        <w:szCs w:val="18"/>
                                                        <w:vertAlign w:val="baseline"/>
                                                        <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                    </w:rPr>
                                                </w:pPr>
                                                <w:r>
                                                    <w:rPr>
                                                        <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体"
                                                                  w:eastAsia="宋体"
                                                                  w:cs="宋体"/>
                                                        <w:b w:val="0"/>
                                                        <w:bCs w:val="0"/>
                                                        <w:sz w:val="18"/>
                                                        <w:szCs w:val="18"/>
                                                        <w:vertAlign w:val="baseline"/>
                                                        <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                                    </w:rPr>
                                                    <w:t>${rowInfo.example!''}</w:t>
                                                </w:r>
                                            </w:p>
                                        </w:tc>
                                    </w:tr>
                                </#list>
                            </w:tbl>
                            <w:p w14:paraId="6322C813">
                                <w:pPr>
                                    <w:keepNext w:val="0"/>
                                    <w:keepLines w:val="0"/>
                                    <w:pageBreakBefore w:val="0"/>
                                    <w:widowControl w:val="0"/>
                                    <w:numPr>
                                        <w:ilvl w:val="0"/>
                                        <w:numId w:val="0"/>
                                    </w:numPr>
                                    <w:kinsoku/>
                                    <w:wordWrap/>
                                    <w:overflowPunct/>
                                    <w:topLinePunct w:val="0"/>
                                    <w:autoSpaceDE/>
                                    <w:autoSpaceDN/>
                                    <w:bidi w:val="0"/>
                                    <w:adjustRightInd/>
                                    <w:snapToGrid/>
                                    <w:spacing w:before="157" w:beforeLines="50" w:after="157" w:afterLines="50"
                                               w:line="240"
                                               w:lineRule="auto"/>
                                    <w:jc w:val="both"/>
                                    <w:textAlignment w:val="auto"/>
                                    <w:rPr>
                                        <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体" w:eastAsia="宋体"
                                                  w:cs="宋体"/>
                                        <w:b/>
                                        <w:bCs/>
                                        <w:sz w:val="21"/>
                                        <w:szCs w:val="21"/>
                                        <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                    </w:rPr>
                                </w:pPr>
                                <w:r>
                                    <w:rPr>
                                        <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体" w:eastAsia="宋体"
                                                  w:cs="宋体"/>
                                        <w:b/>
                                        <w:bCs/>
                                        <w:sz w:val="21"/>
                                        <w:szCs w:val="21"/>
                                        <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                    </w:rPr>
                                    <w:t>示例</w:t>
                                </w:r>
                            </w:p>
                            ${api.responseInfo.responseBodyJson!''}
                        </#if>
                    <#else>
                        <w:p w14:paraId="5E6E8692">
                            <w:pPr>
                                <w:widowControl w:val="0"/>
                                <w:numPr>
                                    <w:ilvl w:val="0"/>
                                    <w:numId w:val="0"/>
                                </w:numPr>
                                <w:jc w:val="both"/>
                                <w:rPr>
                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体" w:eastAsia="宋体"
                                              w:cs="宋体"/>
                                    <w:b w:val="0"/>
                                    <w:bCs/>
                                    <w:sz w:val="18"/>
                                    <w:szCs w:val="18"/>
                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                </w:rPr>
                            </w:pPr>
                            <w:r>
                                <w:rPr>
                                    <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体" w:eastAsia="宋体"
                                              w:cs="宋体"/>
                                    <w:b w:val="0"/>
                                    <w:bCs/>
                                    <w:sz w:val="18"/>
                                    <w:szCs w:val="18"/>
                                    <w:lang w:val="en-US" w:eastAsia="zh-CN"/>
                                </w:rPr>
                                <w:t>无响应参数</w:t>
                            </w:r>
                        </w:p>
                    </#if>
                    </#list>
                    <w:sectPr>
                        <w:pgSz w:w="11906" w:h="16838"/>
                        <w:pgMar w:top="1440" w:right="1800" w:bottom="1440" w:left="1800" w:header="851" w:footer="992"
                                 w:gutter="0"/>
                        <w:cols w:space="425" w:num="1"/>
                        <w:docGrid w:type="lines" w:linePitch="312" w:charSpace="0"/>
                    </w:sectPr>
                </w:body>
            </w:document>
        </pkg:xmlData>
    </pkg:part>
    <pkg:part pkg:name="/docProps/app.xml"
              pkg:contentType="application/vnd.openxmlformats-officedocument.extended-properties+xml">
        <pkg:xmlData>
            <Properties xmlns="http://schemas.openxmlformats.org/officeDocument/2006/extended-properties"
                        xmlns:vt="http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes">
                <Template>Normal.dotm</Template>
                <Pages>3</Pages>
                <Words>0</Words>
                <Characters>0</Characters>
                <Lines>0</Lines>
                <Paragraphs>0</Paragraphs>
                <TotalTime>1</TotalTime>
                <ScaleCrop>false</ScaleCrop>
                <LinksUpToDate>false</LinksUpToDate>
                <CharactersWithSpaces>0</CharactersWithSpaces>
                <Application>WPS Office_7.5.1.8994_F1E327BC-269C-435d-A152-05C5408002CA</Application>
                <DocSecurity>0</DocSecurity>
            </Properties>
        </pkg:xmlData>
    </pkg:part>
    <pkg:part pkg:name="/docProps/core.xml"
              pkg:contentType="application/vnd.openxmlformats-package.core-properties+xml">
        <pkg:xmlData>
            <cp:coreProperties xmlns:cp="http://schemas.openxmlformats.org/package/2006/metadata/core-properties"
                               xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/"
                               xmlns:dcmitype="http://purl.org/dc/dcmitype/"
                               xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                <dcterms:created xsi:type="dcterms:W3CDTF">2025-06-22T15:44:00Z</dcterms:created>
                <dc:creator>zhanglinwei</dc:creator>
                <cp:lastModifiedBy>zhanglinwei</cp:lastModifiedBy>
                <dcterms:modified xsi:type="dcterms:W3CDTF">2025-06-22T21:00:32Z</dcterms:modified>
                <cp:revision>1</cp:revision>
            </cp:coreProperties>
        </pkg:xmlData>
    </pkg:part>
    <pkg:part pkg:name="/docProps/custom.xml"
              pkg:contentType="application/vnd.openxmlformats-officedocument.custom-properties+xml">
        <pkg:xmlData>
            <Properties xmlns="http://schemas.openxmlformats.org/officeDocument/2006/custom-properties"
                        xmlns:vt="http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes">
                <property fmtid="{D5CDD505-2E9C-101B-9397-08002B2CF9AE}" pid="2" name="KSOProductBuildVer">
                    <vt:lpwstr>2052-7.5.1.8994</vt:lpwstr>
                </property>
                <property fmtid="{D5CDD505-2E9C-101B-9397-08002B2CF9AE}" pid="3" name="ICV">
                    <vt:lpwstr>1F25E575345870826FD1576859EFA3D9_43</vt:lpwstr>
                </property>
            </Properties>
        </pkg:xmlData>
    </pkg:part>
    <pkg:part pkg:name="/word/fontTable.xml"
              pkg:contentType="application/vnd.openxmlformats-officedocument.wordprocessingml.fontTable+xml">
        <pkg:xmlData>
            <w:fonts xmlns:mc="http://schemas.openxmlformats.org/markup-compatibility/2006"
                     xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships"
                     xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main"
                     xmlns:w14="http://schemas.microsoft.com/office/word/2010/wordml" mc:Ignorable="w14">
                <w:font w:name="Times New Roman">
                    <w:panose1 w:val="02020603050405020304"/>
                    <w:charset w:val="00"/>
                    <w:family w:val="roman"/>
                    <w:pitch w:val="variable"/>
                    <w:sig w:usb0="20007A87" w:usb1="80000000" w:usb2="00000008" w:usb3="00000000" w:csb0="000001FF"
                           w:csb1="00000000"/>
                </w:font>
                <w:font w:name="宋体">
                    <w:altName w:val="汉仪书宋二KW"/>
                    <w:panose1 w:val="00000000000000000000"/>
                    <w:charset w:val="00"/>
                    <w:family w:val="auto"/>
                    <w:pitch w:val="default"/>
                    <w:sig w:usb0="00000000" w:usb1="00000000" w:usb2="00000000" w:usb3="00000000" w:csb0="00000000"
                           w:csb1="00000000"/>
                </w:font>
                <w:font w:name="Wingdings">
                    <w:panose1 w:val="05000000000000000000"/>
                    <w:charset w:val="02"/>
                    <w:family w:val="auto"/>
                    <w:pitch w:val="default"/>
                    <w:sig w:usb0="00000000" w:usb1="00000000" w:usb2="00000000" w:usb3="00000000" w:csb0="80000000"
                           w:csb1="00000000"/>
                </w:font>
                <w:font w:name="Arial">
                    <w:panose1 w:val="020B0604020202090204"/>
                    <w:charset w:val="01"/>
                    <w:family w:val="swiss"/>
                    <w:pitch w:val="default"/>
                    <w:sig w:usb0="E0000AFF" w:usb1="00007843" w:usb2="00000001" w:usb3="00000000" w:csb0="400001BF"
                           w:csb1="DFF70000"/>
                </w:font>
                <w:font w:name="黑体">
                    <w:altName w:val="汉仪中黑KW"/>
                    <w:panose1 w:val="02010609060101010101"/>
                    <w:charset w:val="86"/>
                    <w:family w:val="auto"/>
                    <w:pitch w:val="default"/>
                    <w:sig w:usb0="800002BF" w:usb1="38CF7CFA" w:usb2="00000016" w:usb3="00000000" w:csb0="00040001"
                           w:csb1="00000000"/>
                </w:font>
                <w:font w:name="Courier New">
                    <w:panose1 w:val="02070409020205090404"/>
                    <w:charset w:val="01"/>
                    <w:family w:val="modern"/>
                    <w:pitch w:val="default"/>
                    <w:sig w:usb0="E0000AFF" w:usb1="40007843" w:usb2="00000001" w:usb3="00000000" w:csb0="400001BF"
                           w:csb1="DFF70000"/>
                </w:font>
                <w:font w:name="Symbol">
                    <w:altName w:val="Kingsoft Sign"/>
                    <w:panose1 w:val="05050102010706020507"/>
                    <w:charset w:val="02"/>
                    <w:family w:val="roman"/>
                    <w:pitch w:val="default"/>
                    <w:sig w:usb0="00000000" w:usb1="00000000" w:usb2="00000000" w:usb3="00000000" w:csb0="80000000"
                           w:csb1="00000000"/>
                </w:font>
                <w:font w:name="Calibri">
                    <w:altName w:val="Helvetica Neue"/>
                    <w:panose1 w:val="020F0502020204030204"/>
                    <w:charset w:val="00"/>
                    <w:family w:val="swiss"/>
                    <w:pitch w:val="default"/>
                    <w:sig w:usb0="00000000" w:usb1="00000000" w:usb2="00000001" w:usb3="00000000" w:csb0="0000019F"
                           w:csb1="00000000"/>
                </w:font>
                <w:font w:name="汉仪书宋二KW">
                    <w:panose1 w:val="00020600040101010101"/>
                    <w:charset w:val="86"/>
                    <w:family w:val="auto"/>
                    <w:pitch w:val="default"/>
                    <w:sig w:usb0="A00002BF" w:usb1="18EF7CFA" w:usb2="00000016" w:usb3="00000000" w:csb0="00040000"
                           w:csb1="00000000"/>
                </w:font>
                <w:font w:name="Helvetica Neue">
                    <w:panose1 w:val="02000503000000020004"/>
                    <w:charset w:val="00"/>
                    <w:family w:val="auto"/>
                    <w:pitch w:val="default"/>
                    <w:sig w:usb0="E50002FF" w:usb1="500079DB" w:usb2="00000010" w:usb3="00000000" w:csb0="00000000"
                           w:csb1="00000000"/>
                </w:font>
                <w:font w:name="汉仪中黑KW">
                    <w:panose1 w:val="00020600040101010101"/>
                    <w:charset w:val="86"/>
                    <w:family w:val="auto"/>
                    <w:pitch w:val="default"/>
                    <w:sig w:usb0="A00002BF" w:usb1="18EF7CFA" w:usb2="00000016" w:usb3="00000000" w:csb0="00040000"
                           w:csb1="00000000"/>
                </w:font>
                <w:font w:name="Kingsoft Sign">
                    <w:panose1 w:val="05050102010706020507"/>
                    <w:charset w:val="00"/>
                    <w:family w:val="auto"/>
                    <w:pitch w:val="default"/>
                    <w:sig w:usb0="00000000" w:usb1="10000000" w:usb2="00000000" w:usb3="00000000" w:csb0="00000001"
                           w:csb1="00000000"/>
                </w:font>
                <w:font w:name="PingFang SC">
                    <w:panose1 w:val="020B0400000000000000"/>
                    <w:charset w:val="86"/>
                    <w:family w:val="auto"/>
                    <w:pitch w:val="default"/>
                    <w:sig w:usb0="A00002FF" w:usb1="7ACFFDFB" w:usb2="00000017" w:usb3="00000000" w:csb0="00040001"
                           w:csb1="00000000"/>
                </w:font>
                <w:font w:name="Tahoma">
                    <w:panose1 w:val="020B0604030504040204"/>
                    <w:charset w:val="00"/>
                    <w:family w:val="auto"/>
                    <w:pitch w:val="default"/>
                    <w:sig w:usb0="E1002AFF" w:usb1="C000605B" w:usb2="00000029" w:usb3="00000000" w:csb0="200101FF"
                           w:csb1="20280000"/>
                </w:font>
                <w:font w:name="Wingdings">
                    <w:panose1 w:val="05000000000000000000"/>
                    <w:charset w:val="00"/>
                    <w:family w:val="auto"/>
                    <w:pitch w:val="default"/>
                    <w:sig w:usb0="00000000" w:usb1="00000000" w:usb2="00000000" w:usb3="00000000" w:csb0="80000000"
                           w:csb1="00000000"/>
                </w:font>
                <w:font w:name="Calibri Light">
                    <w:altName w:val="Helvetica Neue"/>
                    <w:panose1 w:val="00000000000000000000"/>
                    <w:charset w:val="00"/>
                    <w:family w:val="auto"/>
                    <w:pitch w:val="default"/>
                    <w:sig w:usb0="00000000" w:usb1="00000000" w:usb2="00000000" w:usb3="00000000" w:csb0="00000000"
                           w:csb1="00000000"/>
                </w:font>
                <w:font w:name="宋体-简">
                    <w:panose1 w:val="02010800040101010101"/>
                    <w:charset w:val="86"/>
                    <w:family w:val="auto"/>
                    <w:pitch w:val="default"/>
                    <w:sig w:usb0="00000001" w:usb1="080F0000" w:usb2="00000000" w:usb3="00000000" w:csb0="00040000"
                           w:csb1="00000000"/>
                </w:font>
                <w:font w:name="Arial">
                    <w:panose1 w:val="020B0604020202090204"/>
                    <w:charset w:val="00"/>
                    <w:family w:val="auto"/>
                    <w:pitch w:val="default"/>
                    <w:sig w:usb0="E0000AFF" w:usb1="00007843" w:usb2="00000001" w:usb3="00000000" w:csb0="400001BF"
                           w:csb1="DFF70000"/>
                </w:font>
                <w:font w:name="黑体">
                    <w:altName w:val="汉仪中黑KW"/>
                    <w:panose1 w:val="00000000000000000000"/>
                    <w:charset w:val="00"/>
                    <w:family w:val="auto"/>
                    <w:pitch w:val="default"/>
                    <w:sig w:usb0="00000000" w:usb1="00000000" w:usb2="00000000" w:usb3="00000000" w:csb0="00000000"
                           w:csb1="00000000"/>
                </w:font>
                <w:font w:name="汉仪中简黑简">
                    <w:panose1 w:val="00020600040101010101"/>
                    <w:charset w:val="86"/>
                    <w:family w:val="auto"/>
                    <w:pitch w:val="default"/>
                    <w:sig w:usb0="A00002BF" w:usb1="18EF7CFA" w:usb2="00000016" w:usb3="00000000" w:csb0="00040000"
                           w:csb1="00000000"/>
                </w:font>
                <w:font w:name="儷宋 Pro">
                    <w:panose1 w:val="02020300000000000000"/>
                    <w:charset w:val="88"/>
                    <w:family w:val="auto"/>
                    <w:pitch w:val="default"/>
                    <w:sig w:usb0="80000001" w:usb1="28091800" w:usb2="00000016" w:usb3="00000000" w:csb0="00100000"
                           w:csb1="00000000"/>
                </w:font>
                <w:font w:name="Hiragino Sans GB W3">
                    <w:panose1 w:val="020B0300000000000000"/>
                    <w:charset w:val="86"/>
                    <w:family w:val="auto"/>
                    <w:pitch w:val="default"/>
                    <w:sig w:usb0="A00002BF" w:usb1="1ACF7CFA" w:usb2="00000016" w:usb3="00000000" w:csb0="00060007"
                           w:csb1="00000000"/>
                </w:font>
                <w:font w:name="儷黑 Pro">
                    <w:panose1 w:val="020B0500000000000000"/>
                    <w:charset w:val="88"/>
                    <w:family w:val="auto"/>
                    <w:pitch w:val="default"/>
                    <w:sig w:usb0="80000001" w:usb1="28091800" w:usb2="00000016" w:usb3="00000000" w:csb0="00100000"
                           w:csb1="00000000"/>
                </w:font>
                <w:font w:name="Times New Roman Regular">
                    <w:panose1 w:val="02020503050405090304"/>
                    <w:charset w:val="00"/>
                    <w:family w:val="auto"/>
                    <w:pitch w:val="default"/>
                    <w:sig w:usb0="E0000AFF" w:usb1="00007843" w:usb2="00000001" w:usb3="00000000" w:csb0="400001BF"
                           w:csb1="DFF70000"/>
                </w:font>
                <w:font w:name="Telugu MN Regular">
                    <w:panose1 w:val="00000500000000000000"/>
                    <w:charset w:val="00"/>
                    <w:family w:val="auto"/>
                    <w:pitch w:val="default"/>
                    <w:sig w:usb0="00200001" w:usb1="00000000" w:usb2="00000000" w:usb3="00000000" w:csb0="00000001"
                           w:csb1="00000000"/>
                </w:font>
                <w:font w:name="Andale Mono">
                    <w:panose1 w:val="020B0509000000000004"/>
                    <w:charset w:val="00"/>
                    <w:family w:val="auto"/>
                    <w:pitch w:val="default"/>
                    <w:sig w:usb0="00000287" w:usb1="00000000" w:usb2="00000000" w:usb3="00000000" w:csb0="6000009F"
                           w:csb1="DFD70000"/>
                </w:font>
                <w:font w:name="Annai MN">
                    <w:panose1 w:val="00000500000000000000"/>
                    <w:charset w:val="00"/>
                    <w:family w:val="auto"/>
                    <w:pitch w:val="default"/>
                    <w:sig w:usb0="20108007" w:usb1="02000000" w:usb2="00000000" w:usb3="00000000" w:csb0="20000193"
                           w:csb1="00000000"/>
                </w:font>
                <w:font w:name="monospace">
                    <w:altName w:val="苹方-简"/>
                    <w:panose1 w:val="00000000000000000000"/>
                    <w:charset w:val="00"/>
                    <w:family w:val="auto"/>
                    <w:pitch w:val="default"/>
                    <w:sig w:usb0="00000000" w:usb1="00000000" w:usb2="00000000" w:usb3="00000000" w:csb0="00000000"
                           w:csb1="00000000"/>
                </w:font>
                <w:font w:name="苹方-简">
                    <w:panose1 w:val="020B0400000000000000"/>
                    <w:charset w:val="86"/>
                    <w:family w:val="auto"/>
                    <w:pitch w:val="default"/>
                    <w:sig w:usb0="A00002FF" w:usb1="7ACFFDFB" w:usb2="00000017" w:usb3="00000000" w:csb0="00040001"
                           w:csb1="00000000"/>
                </w:font>
            </w:fonts>
        </pkg:xmlData>
    </pkg:part>
    <pkg:part pkg:name="/word/settings.xml"
              pkg:contentType="application/vnd.openxmlformats-officedocument.wordprocessingml.settings+xml">
        <pkg:xmlData>
            <w:settings xmlns:mc="http://schemas.openxmlformats.org/markup-compatibility/2006"
                        xmlns:o="urn:schemas-microsoft-com:office:office"
                        xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships"
                        xmlns:m="http://schemas.openxmlformats.org/officeDocument/2006/math"
                        xmlns:v="urn:schemas-microsoft-com:vml" xmlns:w10="urn:schemas-microsoft-com:office:word"
                        xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main"
                        xmlns:w14="http://schemas.microsoft.com/office/word/2010/wordml"
                        xmlns:sl="http://schemas.openxmlformats.org/schemaLibrary/2006/main"
                        xmlns:wpsCustomData="http://www.wps.cn/officeDocument/2013/wpsCustomData" mc:Ignorable="w14">
                <w:zoom w:percent="220"/>
                <w:embedSystemFonts/>
                <w:bordersDoNotSurroundHeader w:val="0"/>
                <w:bordersDoNotSurroundFooter w:val="0"/>
                <w:documentProtection w:enforcement="0"/>
                <w:defaultTabStop w:val="420"/>
                <w:drawingGridVerticalSpacing w:val="156"/>
                <w:displayHorizontalDrawingGridEvery w:val="1"/>
                <w:displayVerticalDrawingGridEvery w:val="1"/>
                <w:noPunctuationKerning w:val="1"/>
                <w:characterSpacingControl w:val="compressPunctuation"/>
                <w:compat>
                    <w:spaceForUL/>
                    <w:balanceSingleByteDoubleByteWidth/>
                    <w:doNotLeaveBackslashAlone/>
                    <w:ulTrailSpace/>
                    <w:doNotExpandShiftReturn/>
                    <w:adjustLineHeightInTable/>
                    <w:doNotWrapTextWithPunct/>
                    <w:doNotUseEastAsianBreakRules/>
                    <w:useFELayout/>
                    <w:compatSetting w:name="compatibilityMode" w:uri="http://schemas.microsoft.com/office/word"
                                     w:val="14"/>
                    <w:compatSetting w:name="overrideTableStyleFontSizeAndJustification"
                                     w:uri="http://schemas.microsoft.com/office/word" w:val="1"/>
                    <w:compatSetting w:name="enableOpenTypeFeatures" w:uri="http://schemas.microsoft.com/office/word"
                                     w:val="1"/>
                    <w:compatSetting w:name="doNotFlipMirrorIndents" w:uri="http://schemas.microsoft.com/office/word"
                                     w:val="1"/>
                </w:compat>
                <w:rsids>
                    <w:rsidRoot w:val="FD7FD1AD"/>
                    <w:rsid w:val="3E67F7AB"/>
                    <w:rsid w:val="A5B5DF5E"/>
                    <w:rsid w:val="E7FF5A0F"/>
                    <w:rsid w:val="FD7FD1AD"/>
                    <w:rsid w:val="FDDB808C"/>
                    <w:rsid w:val="FFF1F3C3"/>
                </w:rsids>
                <m:mathPr>
                    <m:mathFont m:val="Cambria Math"/>
                    <m:brkBin m:val="before"/>
                    <m:brkBinSub m:val="--"/>
                    <m:smallFrac m:val="0"/>
                    <m:dispDef/>
                    <m:lMargin m:val="0"/>
                    <m:rMargin m:val="0"/>
                    <m:defJc m:val="centerGroup"/>
                    <m:wrapIndent m:val="1440"/>
                    <m:intLim m:val="subSup"/>
                    <m:naryLim m:val="undOvr"/>
                </m:mathPr>
                <w:themeFontLang w:val="en-US" w:eastAsia="zh-CN"/>
                <w:clrSchemeMapping w:bg1="light1" w:t1="dark1" w:bg2="light2" w:t2="dark2" w:accent1="accent1"
                                    w:accent2="accent2" w:accent3="accent3" w:accent4="accent4" w:accent5="accent5"
                                    w:accent6="accent6" w:hyperlink="hyperlink"
                                    w:followedHyperlink="followedHyperlink"/>
                <w:doNotIncludeSubdocsInStats/>
            </w:settings>
        </pkg:xmlData>
    </pkg:part>
    <pkg:part pkg:name="/word/styles.xml"
              pkg:contentType="application/vnd.openxmlformats-officedocument.wordprocessingml.styles+xml">
        <pkg:xmlData>
            <w:styles xmlns:mc="http://schemas.openxmlformats.org/markup-compatibility/2006"
                      xmlns:o="urn:schemas-microsoft-com:office:office"
                      xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships"
                      xmlns:m="http://schemas.openxmlformats.org/officeDocument/2006/math"
                      xmlns:v="urn:schemas-microsoft-com:vml"
                      xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main"
                      xmlns:w14="http://schemas.microsoft.com/office/word/2010/wordml"
                      xmlns:w10="urn:schemas-microsoft-com:office:word"
                      xmlns:sl="http://schemas.openxmlformats.org/schemaLibrary/2006/main"
                      xmlns:wpsCustomData="http://www.wps.cn/officeDocument/2013/wpsCustomData" mc:Ignorable="w14">
                <w:docDefaults>
                    <w:rPrDefault>
                        <w:rPr>
                            <w:rFonts w:ascii="Times New Roman" w:hAnsi="Times New Roman" w:eastAsia="宋体"
                                      w:cs="Times New Roman"/>
                        </w:rPr>
                    </w:rPrDefault>
                    <w:pPrDefault/>
                </w:docDefaults>
                <w:latentStyles w:count="260" w:defQFormat="0" w:defUnhideWhenUsed="1" w:defSemiHidden="1"
                                w:defUIPriority="99" w:defLockedState="0">
                    <w:lsdException w:qFormat="1" w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0"
                                    w:name="Normal"/>
                    <w:lsdException w:qFormat="1" w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0"
                                    w:name="heading 1"/>
                    <w:lsdException w:qFormat="1" w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0"
                                    w:name="heading 2"/>
                    <w:lsdException w:qFormat="1" w:uiPriority="0" w:semiHidden="0" w:name="heading 3"/>
                    <w:lsdException w:qFormat="1" w:uiPriority="0" w:semiHidden="0" w:name="heading 4"/>
                    <w:lsdException w:qFormat="1" w:uiPriority="0" w:name="heading 5"/>
                    <w:lsdException w:qFormat="1" w:uiPriority="0" w:name="heading 6"/>
                    <w:lsdException w:qFormat="1" w:uiPriority="0" w:name="heading 7"/>
                    <w:lsdException w:qFormat="1" w:uiPriority="0" w:name="heading 8"/>
                    <w:lsdException w:qFormat="1" w:uiPriority="0" w:name="heading 9"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="index 1"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="index 2"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="index 3"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="index 4"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="index 5"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="index 6"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="index 7"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="index 8"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="index 9"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="toc 1"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="toc 2"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="toc 3"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="toc 4"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="toc 5"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="toc 6"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="toc 7"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="toc 8"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="toc 9"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Normal Indent"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="footnote text"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="annotation text"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="header"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="footer"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="index heading"/>
                    <w:lsdException w:qFormat="1" w:uiPriority="0" w:name="caption"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="table of figures"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="envelope address"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="envelope return"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="footnote reference"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0"
                                    w:name="annotation reference"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="line number"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="page number"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="endnote reference"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="endnote text"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0"
                                    w:name="table of authorities"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="macro"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="toa heading"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="List"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="List Bullet"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="List Number"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="List 2"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="List 3"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="List 4"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="List 5"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="List Bullet 2"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="List Bullet 3"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="List Bullet 4"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="List Bullet 5"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="List Number 2"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="List Number 3"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="List Number 4"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="List Number 5"/>
                    <w:lsdException w:qFormat="1" w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0"
                                    w:name="Title"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Closing"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Signature"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:name="Default Paragraph Font"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Body Text"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Body Text Indent"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="List Continue"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="List Continue 2"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="List Continue 3"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="List Continue 4"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="List Continue 5"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Message Header"/>
                    <w:lsdException w:qFormat="1" w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0"
                                    w:name="Subtitle"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Salutation"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Date"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0"
                                    w:name="Body Text First Indent"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0"
                                    w:name="Body Text First Indent 2"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Note Heading"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Body Text 2"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Body Text 3"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Body Text Indent 2"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Body Text Indent 3"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Block Text"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Hyperlink"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="FollowedHyperlink"/>
                    <w:lsdException w:qFormat="1" w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0"
                                    w:name="Strong"/>
                    <w:lsdException w:qFormat="1" w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0"
                                    w:name="Emphasis"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Document Map"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Plain Text"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="E-mail Signature"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Normal (Web)"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="HTML Acronym"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="HTML Address"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="HTML Cite"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="HTML Code"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="HTML Definition"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="HTML Keyboard"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="HTML Preformatted"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="HTML Sample"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="HTML Typewriter"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="HTML Variable"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:name="Normal Table"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="annotation subject"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table Simple 1"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table Simple 2"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table Simple 3"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table Classic 1"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table Classic 2"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table Classic 3"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table Classic 4"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table Colorful 1"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table Colorful 2"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table Colorful 3"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table Columns 1"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table Columns 2"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table Columns 3"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table Columns 4"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table Columns 5"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table Grid 1"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table Grid 2"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table Grid 3"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table Grid 4"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table Grid 5"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table Grid 6"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table Grid 7"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table Grid 8"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table List 1"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table List 2"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table List 3"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table List 4"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table List 5"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table List 6"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table List 7"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table List 8"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table 3D effects 1"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table 3D effects 2"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table 3D effects 3"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table Contemporary"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table Elegant"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table Professional"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table Subtle 1"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table Subtle 2"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table Web 1"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table Web 2"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table Web 3"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Balloon Text"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table Grid"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="0" w:semiHidden="0" w:name="Table Theme"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="60" w:semiHidden="0" w:name="Light Shading"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="61" w:semiHidden="0" w:name="Light List"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="62" w:semiHidden="0" w:name="Light Grid"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="63" w:semiHidden="0" w:name="Medium Shading 1"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="64" w:semiHidden="0" w:name="Medium Shading 2"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="65" w:semiHidden="0" w:name="Medium List 1"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="66" w:semiHidden="0" w:name="Medium List 2"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="67" w:semiHidden="0" w:name="Medium Grid 1"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="68" w:semiHidden="0" w:name="Medium Grid 2"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="69" w:semiHidden="0" w:name="Medium Grid 3"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="70" w:semiHidden="0" w:name="Dark List"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="71" w:semiHidden="0" w:name="Colorful Shading"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="72" w:semiHidden="0" w:name="Colorful List"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="73" w:semiHidden="0" w:name="Colorful Grid"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="60" w:semiHidden="0"
                                    w:name="Light Shading Accent 1"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="61" w:semiHidden="0"
                                    w:name="Light List Accent 1"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="62" w:semiHidden="0"
                                    w:name="Light Grid Accent 1"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="63" w:semiHidden="0"
                                    w:name="Medium Shading 1 Accent 1"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="64" w:semiHidden="0"
                                    w:name="Medium Shading 2 Accent 1"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="65" w:semiHidden="0"
                                    w:name="Medium List 1 Accent 1"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="66" w:semiHidden="0"
                                    w:name="Medium List 2 Accent 1"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="67" w:semiHidden="0"
                                    w:name="Medium Grid 1 Accent 1"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="68" w:semiHidden="0"
                                    w:name="Medium Grid 2 Accent 1"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="69" w:semiHidden="0"
                                    w:name="Medium Grid 3 Accent 1"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="70" w:semiHidden="0"
                                    w:name="Dark List Accent 1"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="71" w:semiHidden="0"
                                    w:name="Colorful Shading Accent 1"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="72" w:semiHidden="0"
                                    w:name="Colorful List Accent 1"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="73" w:semiHidden="0"
                                    w:name="Colorful Grid Accent 1"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="60" w:semiHidden="0"
                                    w:name="Light Shading Accent 2"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="61" w:semiHidden="0"
                                    w:name="Light List Accent 2"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="62" w:semiHidden="0"
                                    w:name="Light Grid Accent 2"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="63" w:semiHidden="0"
                                    w:name="Medium Shading 1 Accent 2"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="64" w:semiHidden="0"
                                    w:name="Medium Shading 2 Accent 2"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="65" w:semiHidden="0"
                                    w:name="Medium List 1 Accent 2"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="66" w:semiHidden="0"
                                    w:name="Medium List 2 Accent 2"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="67" w:semiHidden="0"
                                    w:name="Medium Grid 1 Accent 2"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="68" w:semiHidden="0"
                                    w:name="Medium Grid 2 Accent 2"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="69" w:semiHidden="0"
                                    w:name="Medium Grid 3 Accent 2"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="70" w:semiHidden="0"
                                    w:name="Dark List Accent 2"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="71" w:semiHidden="0"
                                    w:name="Colorful Shading Accent 2"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="72" w:semiHidden="0"
                                    w:name="Colorful List Accent 2"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="73" w:semiHidden="0"
                                    w:name="Colorful Grid Accent 2"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="60" w:semiHidden="0"
                                    w:name="Light Shading Accent 3"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="61" w:semiHidden="0"
                                    w:name="Light List Accent 3"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="62" w:semiHidden="0"
                                    w:name="Light Grid Accent 3"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="63" w:semiHidden="0"
                                    w:name="Medium Shading 1 Accent 3"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="64" w:semiHidden="0"
                                    w:name="Medium Shading 2 Accent 3"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="65" w:semiHidden="0"
                                    w:name="Medium List 1 Accent 3"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="66" w:semiHidden="0"
                                    w:name="Medium List 2 Accent 3"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="67" w:semiHidden="0"
                                    w:name="Medium Grid 1 Accent 3"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="68" w:semiHidden="0"
                                    w:name="Medium Grid 2 Accent 3"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="69" w:semiHidden="0"
                                    w:name="Medium Grid 3 Accent 3"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="70" w:semiHidden="0"
                                    w:name="Dark List Accent 3"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="71" w:semiHidden="0"
                                    w:name="Colorful Shading Accent 3"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="72" w:semiHidden="0"
                                    w:name="Colorful List Accent 3"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="73" w:semiHidden="0"
                                    w:name="Colorful Grid Accent 3"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="60" w:semiHidden="0"
                                    w:name="Light Shading Accent 4"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="61" w:semiHidden="0"
                                    w:name="Light List Accent 4"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="62" w:semiHidden="0"
                                    w:name="Light Grid Accent 4"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="63" w:semiHidden="0"
                                    w:name="Medium Shading 1 Accent 4"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="64" w:semiHidden="0"
                                    w:name="Medium Shading 2 Accent 4"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="65" w:semiHidden="0"
                                    w:name="Medium List 1 Accent 4"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="66" w:semiHidden="0"
                                    w:name="Medium List 2 Accent 4"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="67" w:semiHidden="0"
                                    w:name="Medium Grid 1 Accent 4"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="68" w:semiHidden="0"
                                    w:name="Medium Grid 2 Accent 4"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="69" w:semiHidden="0"
                                    w:name="Medium Grid 3 Accent 4"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="70" w:semiHidden="0"
                                    w:name="Dark List Accent 4"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="71" w:semiHidden="0"
                                    w:name="Colorful Shading Accent 4"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="72" w:semiHidden="0"
                                    w:name="Colorful List Accent 4"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="73" w:semiHidden="0"
                                    w:name="Colorful Grid Accent 4"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="60" w:semiHidden="0"
                                    w:name="Light Shading Accent 5"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="61" w:semiHidden="0"
                                    w:name="Light List Accent 5"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="62" w:semiHidden="0"
                                    w:name="Light Grid Accent 5"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="63" w:semiHidden="0"
                                    w:name="Medium Shading 1 Accent 5"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="64" w:semiHidden="0"
                                    w:name="Medium Shading 2 Accent 5"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="65" w:semiHidden="0"
                                    w:name="Medium List 1 Accent 5"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="66" w:semiHidden="0"
                                    w:name="Medium List 2 Accent 5"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="67" w:semiHidden="0"
                                    w:name="Medium Grid 1 Accent 5"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="68" w:semiHidden="0"
                                    w:name="Medium Grid 2 Accent 5"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="69" w:semiHidden="0"
                                    w:name="Medium Grid 3 Accent 5"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="70" w:semiHidden="0"
                                    w:name="Dark List Accent 5"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="71" w:semiHidden="0"
                                    w:name="Colorful Shading Accent 5"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="72" w:semiHidden="0"
                                    w:name="Colorful List Accent 5"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="73" w:semiHidden="0"
                                    w:name="Colorful Grid Accent 5"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="60" w:semiHidden="0"
                                    w:name="Light Shading Accent 6"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="61" w:semiHidden="0"
                                    w:name="Light List Accent 6"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="62" w:semiHidden="0"
                                    w:name="Light Grid Accent 6"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="63" w:semiHidden="0"
                                    w:name="Medium Shading 1 Accent 6"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="64" w:semiHidden="0"
                                    w:name="Medium Shading 2 Accent 6"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="65" w:semiHidden="0"
                                    w:name="Medium List 1 Accent 6"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="66" w:semiHidden="0"
                                    w:name="Medium List 2 Accent 6"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="67" w:semiHidden="0"
                                    w:name="Medium Grid 1 Accent 6"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="68" w:semiHidden="0"
                                    w:name="Medium Grid 2 Accent 6"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="69" w:semiHidden="0"
                                    w:name="Medium Grid 3 Accent 6"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="70" w:semiHidden="0"
                                    w:name="Dark List Accent 6"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="71" w:semiHidden="0"
                                    w:name="Colorful Shading Accent 6"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="72" w:semiHidden="0"
                                    w:name="Colorful List Accent 6"/>
                    <w:lsdException w:unhideWhenUsed="0" w:uiPriority="73" w:semiHidden="0"
                                    w:name="Colorful Grid Accent 6"/>
                </w:latentStyles>
                <w:style w:type="paragraph" w:default="1" w:styleId="1">
                    <w:name w:val="Normal"/>
                    <w:qFormat/>
                    <w:uiPriority w:val="0"/>
                    <w:pPr>
                        <w:widowControl w:val="0"/>
                        <w:jc w:val="both"/>
                    </w:pPr>
                    <w:rPr>
                        <w:rFonts w:asciiTheme="minorHAnsi" w:hAnsiTheme="minorHAnsi" w:eastAsiaTheme="minorEastAsia"
                                  w:cstheme="minorBidi"/>
                        <w:kern w:val="2"/>
                        <w:sz w:val="21"/>
                        <w:szCs w:val="24"/>
                        <w:lang w:val="en-US" w:eastAsia="zh-CN" w:bidi="ar-SA"/>
                    </w:rPr>
                </w:style>
                <w:style w:type="paragraph" w:styleId="2">
                    <w:name w:val="heading 1"/>
                    <w:basedOn w:val="1"/>
                    <w:next w:val="1"/>
                    <w:qFormat/>
                    <w:uiPriority w:val="0"/>
                    <w:pPr>
                        <w:keepNext/>
                        <w:keepLines/>
                        <w:spacing w:before="340" w:beforeLines="0" w:beforeAutospacing="0" w:after="330"
                                   w:afterLines="0" w:afterAutospacing="0" w:line="576" w:lineRule="auto"/>
                        <w:outlineLvl w:val="0"/>
                    </w:pPr>
                    <w:rPr>
                        <w:b/>
                        <w:kern w:val="44"/>
                        <w:sz w:val="44"/>
                    </w:rPr>
                </w:style>
                <w:style w:type="paragraph" w:styleId="3">
                    <w:name w:val="heading 2"/>
                    <w:basedOn w:val="1"/>
                    <w:next w:val="1"/>
                    <w:qFormat/>
                    <w:uiPriority w:val="0"/>
                    <w:pPr>
                        <w:keepNext/>
                        <w:keepLines/>
                        <w:spacing w:before="260" w:beforeLines="0" w:beforeAutospacing="0" w:after="260"
                                   w:afterLines="0" w:afterAutospacing="0" w:line="413" w:lineRule="auto"/>
                        <w:outlineLvl w:val="1"/>
                    </w:pPr>
                    <w:rPr>
                        <w:rFonts w:ascii="Arial" w:hAnsi="Arial" w:eastAsia="黑体"/>
                        <w:b/>
                        <w:sz w:val="32"/>
                    </w:rPr>
                </w:style>
                <w:style w:type="paragraph" w:styleId="4">
                    <w:name w:val="heading 3"/>
                    <w:basedOn w:val="1"/>
                    <w:next w:val="1"/>
                    <w:unhideWhenUsed/>
                    <w:qFormat/>
                    <w:uiPriority w:val="0"/>
                    <w:pPr>
                        <w:keepNext/>
                        <w:keepLines/>
                        <w:spacing w:before="260" w:beforeLines="0" w:beforeAutospacing="0" w:after="260"
                                   w:afterLines="0" w:afterAutospacing="0" w:line="413" w:lineRule="auto"/>
                        <w:outlineLvl w:val="2"/>
                    </w:pPr>
                    <w:rPr>
                        <w:b/>
                        <w:sz w:val="32"/>
                    </w:rPr>
                </w:style>
                <w:style w:type="paragraph" w:styleId="5">
                    <w:name w:val="heading 4"/>
                    <w:basedOn w:val="1"/>
                    <w:next w:val="1"/>
                    <w:unhideWhenUsed/>
                    <w:qFormat/>
                    <w:uiPriority w:val="0"/>
                    <w:pPr>
                        <w:keepNext/>
                        <w:keepLines/>
                        <w:spacing w:before="280" w:beforeLines="0" w:beforeAutospacing="0" w:after="290"
                                   w:afterLines="0" w:afterAutospacing="0" w:line="372" w:lineRule="auto"/>
                        <w:outlineLvl w:val="3"/>
                    </w:pPr>
                    <w:rPr>
                        <w:rFonts w:ascii="Arial" w:hAnsi="Arial" w:eastAsia="黑体"/>
                        <w:b/>
                        <w:sz w:val="28"/>
                    </w:rPr>
                </w:style>
                <w:style w:type="character" w:default="1" w:styleId="9">
                    <w:name w:val="Default Paragraph Font"/>
                    <w:semiHidden/>
                    <w:uiPriority w:val="0"/>
                </w:style>
                <w:style w:type="table" w:default="1" w:styleId="7">
                    <w:name w:val="Normal Table"/>
                    <w:semiHidden/>
                    <w:uiPriority w:val="0"/>
                    <w:tblPr>
                        <w:tblCellMar>
                            <w:top w:w="0" w:type="dxa"/>
                            <w:left w:w="108" w:type="dxa"/>
                            <w:bottom w:w="0" w:type="dxa"/>
                            <w:right w:w="108" w:type="dxa"/>
                        </w:tblCellMar>
                    </w:tblPr>
                </w:style>
                <w:style w:type="paragraph" w:styleId="6">
                    <w:name w:val="HTML Preformatted"/>
                    <w:basedOn w:val="1"/>
                    <w:uiPriority w:val="0"/>
                    <w:pPr>
                        <w:tabs>
                            <w:tab w:val="left" w:pos="916"/>
                            <w:tab w:val="left" w:pos="1832"/>
                            <w:tab w:val="left" w:pos="2748"/>
                            <w:tab w:val="left" w:pos="3664"/>
                            <w:tab w:val="left" w:pos="4580"/>
                            <w:tab w:val="left" w:pos="5496"/>
                            <w:tab w:val="left" w:pos="6412"/>
                            <w:tab w:val="left" w:pos="7328"/>
                            <w:tab w:val="left" w:pos="8244"/>
                            <w:tab w:val="left" w:pos="9160"/>
                            <w:tab w:val="left" w:pos="10076"/>
                            <w:tab w:val="left" w:pos="10992"/>
                            <w:tab w:val="left" w:pos="11908"/>
                            <w:tab w:val="left" w:pos="12824"/>
                            <w:tab w:val="left" w:pos="13740"/>
                            <w:tab w:val="left" w:pos="14656"/>
                        </w:tabs>
                        <w:jc w:val="left"/>
                    </w:pPr>
                    <w:rPr>
                        <w:rFonts w:hint="eastAsia" w:ascii="宋体" w:hAnsi="宋体" w:eastAsia="宋体" w:cs="宋体"/>
                        <w:kern w:val="0"/>
                        <w:sz w:val="24"/>
                        <w:szCs w:val="24"/>
                        <w:lang w:val="en-US" w:eastAsia="zh-CN" w:bidi="ar"/>
                    </w:rPr>
                </w:style>
                <w:style w:type="table" w:styleId="8">
                    <w:name w:val="Table Grid"/>
                    <w:basedOn w:val="7"/>
                    <w:uiPriority w:val="0"/>
                    <w:pPr>
                        <w:widowControl w:val="0"/>
                        <w:jc w:val="both"/>
                    </w:pPr>
                    <w:tblPr>
                        <w:tblBorders>
                            <w:top w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                            <w:left w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                            <w:bottom w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                            <w:right w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                            <w:insideH w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                            <w:insideV w:val="single" w:color="auto" w:sz="4" w:space="0"/>
                        </w:tblBorders>
                    </w:tblPr>
                </w:style>
            </w:styles>
        </pkg:xmlData>
    </pkg:part>
    <pkg:part pkg:name="/word/theme/theme1.xml"
              pkg:contentType="application/vnd.openxmlformats-officedocument.theme+xml">
        <pkg:xmlData>
            <a:theme xmlns:a="http://schemas.openxmlformats.org/drawingml/2006/main" name="WPS">
                <a:themeElements>
                    <a:clrScheme name="WPS">
                        <a:dk1>
                            <a:sysClr val="windowText" lastClr="000000"/>
                        </a:dk1>
                        <a:lt1>
                            <a:sysClr val="window" lastClr="FFFFFF"/>
                        </a:lt1>
                        <a:dk2>
                            <a:srgbClr val="44546A"/>
                        </a:dk2>
                        <a:lt2>
                            <a:srgbClr val="E7E6E6"/>
                        </a:lt2>
                        <a:accent1>
                            <a:srgbClr val="4874CB"/>
                        </a:accent1>
                        <a:accent2>
                            <a:srgbClr val="EE822F"/>
                        </a:accent2>
                        <a:accent3>
                            <a:srgbClr val="F2BA02"/>
                        </a:accent3>
                        <a:accent4>
                            <a:srgbClr val="75BD42"/>
                        </a:accent4>
                        <a:accent5>
                            <a:srgbClr val="30C0B4"/>
                        </a:accent5>
                        <a:accent6>
                            <a:srgbClr val="E54C5E"/>
                        </a:accent6>
                        <a:hlink>
                            <a:srgbClr val="0026E5"/>
                        </a:hlink>
                        <a:folHlink>
                            <a:srgbClr val="7E1FAD"/>
                        </a:folHlink>
                    </a:clrScheme>
                    <a:fontScheme name="WPS">
                        <a:majorFont>
                            <a:latin typeface="Calibri Light"/>
                            <a:ea typeface=""/>
                            <a:cs typeface=""/>
                            <a:font script="Jpan" typeface="ＭＳ ゴシック"/>
                            <a:font script="Hang" typeface="맑은 고딕"/>
                            <a:font script="Hans" typeface="宋体"/>
                            <a:font script="Hant" typeface="新細明體"/>
                            <a:font script="Arab" typeface="Times New Roman"/>
                            <a:font script="Hebr" typeface="Times New Roman"/>
                            <a:font script="Thai" typeface="Angsana New"/>
                            <a:font script="Ethi" typeface="Nyala"/>
                            <a:font script="Beng" typeface="Vrinda"/>
                            <a:font script="Gujr" typeface="Shruti"/>
                            <a:font script="Khmr" typeface="MoolBoran"/>
                            <a:font script="Knda" typeface="Tunga"/>
                            <a:font script="Guru" typeface="Raavi"/>
                            <a:font script="Cans" typeface="Euphemia"/>
                            <a:font script="Cher" typeface="Plantagenet Cherokee"/>
                            <a:font script="Yiii" typeface="Microsoft Yi Baiti"/>
                            <a:font script="Tibt" typeface="Microsoft Himalaya"/>
                            <a:font script="Thaa" typeface="MV Boli"/>
                            <a:font script="Deva" typeface="Mangal"/>
                            <a:font script="Telu" typeface="Gautami"/>
                            <a:font script="Taml" typeface="Latha"/>
                            <a:font script="Syrc" typeface="Estrangelo Edessa"/>
                            <a:font script="Orya" typeface="Kalinga"/>
                            <a:font script="Mlym" typeface="Kartika"/>
                            <a:font script="Laoo" typeface="DokChampa"/>
                            <a:font script="Sinh" typeface="Iskoola Pota"/>
                            <a:font script="Mong" typeface="Mongolian Baiti"/>
                            <a:font script="Viet" typeface="Times New Roman"/>
                            <a:font script="Uigh" typeface="Microsoft Uighur"/>
                            <a:font script="Geor" typeface="Sylfaen"/>
                        </a:majorFont>
                        <a:minorFont>
                            <a:latin typeface="Calibri"/>
                            <a:ea typeface=""/>
                            <a:cs typeface=""/>
                            <a:font script="Jpan" typeface="ＭＳ 明朝"/>
                            <a:font script="Hang" typeface="맑은 고딕"/>
                            <a:font script="Hans" typeface="宋体"/>
                            <a:font script="Hant" typeface="新細明體"/>
                            <a:font script="Arab" typeface="Arial"/>
                            <a:font script="Hebr" typeface="Arial"/>
                            <a:font script="Thai" typeface="Cordia New"/>
                            <a:font script="Ethi" typeface="Nyala"/>
                            <a:font script="Beng" typeface="Vrinda"/>
                            <a:font script="Gujr" typeface="Shruti"/>
                            <a:font script="Khmr" typeface="DaunPenh"/>
                            <a:font script="Knda" typeface="Tunga"/>
                            <a:font script="Guru" typeface="Raavi"/>
                            <a:font script="Cans" typeface="Euphemia"/>
                            <a:font script="Cher" typeface="Plantagenet Cherokee"/>
                            <a:font script="Yiii" typeface="Microsoft Yi Baiti"/>
                            <a:font script="Tibt" typeface="Microsoft Himalaya"/>
                            <a:font script="Thaa" typeface="MV Boli"/>
                            <a:font script="Deva" typeface="Mangal"/>
                            <a:font script="Telu" typeface="Gautami"/>
                            <a:font script="Taml" typeface="Latha"/>
                            <a:font script="Syrc" typeface="Estrangelo Edessa"/>
                            <a:font script="Orya" typeface="Kalinga"/>
                            <a:font script="Mlym" typeface="Kartika"/>
                            <a:font script="Laoo" typeface="DokChampa"/>
                            <a:font script="Sinh" typeface="Iskoola Pota"/>
                            <a:font script="Mong" typeface="Mongolian Baiti"/>
                            <a:font script="Viet" typeface="Arial"/>
                            <a:font script="Uigh" typeface="Microsoft Uighur"/>
                            <a:font script="Geor" typeface="Sylfaen"/>
                        </a:minorFont>
                    </a:fontScheme>
                    <a:fmtScheme name="WPS">
                        <a:fillStyleLst>
                            <a:solidFill>
                                <a:schemeClr val="phClr"/>
                            </a:solidFill>
                            <a:gradFill>
                                <a:gsLst>
                                    <a:gs pos="0">
                                        <a:schemeClr val="phClr">
                                            <a:lumOff val="17500"/>
                                        </a:schemeClr>
                                    </a:gs>
                                    <a:gs pos="100000">
                                        <a:schemeClr val="phClr"/>
                                    </a:gs>
                                </a:gsLst>
                                <a:lin ang="2700000" scaled="0"/>
                            </a:gradFill>
                            <a:gradFill>
                                <a:gsLst>
                                    <a:gs pos="0">
                                        <a:schemeClr val="phClr">
                                            <a:hueOff val="-2520000"/>
                                        </a:schemeClr>
                                    </a:gs>
                                    <a:gs pos="100000">
                                        <a:schemeClr val="phClr"/>
                                    </a:gs>
                                </a:gsLst>
                                <a:lin ang="2700000" scaled="0"/>
                            </a:gradFill>
                        </a:fillStyleLst>
                        <a:lnStyleLst>
                            <a:ln w="12700" cap="flat" cmpd="sng" algn="ctr">
                                <a:solidFill>
                                    <a:schemeClr val="phClr"/>
                                </a:solidFill>
                                <a:prstDash val="solid"/>
                                <a:miter lim="800000"/>
                            </a:ln>
                            <a:ln w="12700" cap="flat" cmpd="sng" algn="ctr">
                                <a:solidFill>
                                    <a:schemeClr val="phClr"/>
                                </a:solidFill>
                                <a:prstDash val="solid"/>
                                <a:miter lim="800000"/>
                            </a:ln>
                            <a:ln w="12700" cap="flat" cmpd="sng" algn="ctr">
                                <a:gradFill>
                                    <a:gsLst>
                                        <a:gs pos="0">
                                            <a:schemeClr val="phClr">
                                                <a:hueOff val="-4200000"/>
                                            </a:schemeClr>
                                        </a:gs>
                                        <a:gs pos="100000">
                                            <a:schemeClr val="phClr"/>
                                        </a:gs>
                                    </a:gsLst>
                                    <a:lin ang="2700000" scaled="1"/>
                                </a:gradFill>
                                <a:prstDash val="solid"/>
                                <a:miter lim="800000"/>
                            </a:ln>
                        </a:lnStyleLst>
                        <a:effectStyleLst>
                            <a:effectStyle>
                                <a:effectLst>
                                    <a:outerShdw blurRad="101600" dist="50800" dir="5400000" algn="ctr"
                                                 rotWithShape="0">
                                        <a:schemeClr val="phClr">
                                            <a:alpha val="60000"/>
                                        </a:schemeClr>
                                    </a:outerShdw>
                                </a:effectLst>
                            </a:effectStyle>
                            <a:effectStyle>
                                <a:effectLst>
                                    <a:reflection stA="50000" endA="300" endPos="40000" dist="25400" dir="5400000"
                                                  sy="-100000" algn="bl" rotWithShape="0"/>
                                </a:effectLst>
                            </a:effectStyle>
                            <a:effectStyle>
                                <a:effectLst>
                                    <a:outerShdw blurRad="57150" dist="19050" dir="5400000" algn="ctr" rotWithShape="0">
                                        <a:srgbClr val="000000">
                                            <a:alpha val="63000"/>
                                        </a:srgbClr>
                                    </a:outerShdw>
                                </a:effectLst>
                            </a:effectStyle>
                        </a:effectStyleLst>
                        <a:bgFillStyleLst>
                            <a:solidFill>
                                <a:schemeClr val="phClr"/>
                            </a:solidFill>
                            <a:solidFill>
                                <a:schemeClr val="phClr">
                                    <a:tint val="95000"/>
                                    <a:satMod val="170000"/>
                                </a:schemeClr>
                            </a:solidFill>
                            <a:gradFill rotWithShape="1">
                                <a:gsLst>
                                    <a:gs pos="0">
                                        <a:schemeClr val="phClr">
                                            <a:tint val="93000"/>
                                            <a:satMod val="150000"/>
                                            <a:shade val="98000"/>
                                            <a:lumMod val="102000"/>
                                        </a:schemeClr>
                                    </a:gs>
                                    <a:gs pos="50000">
                                        <a:schemeClr val="phClr">
                                            <a:tint val="98000"/>
                                            <a:satMod val="130000"/>
                                            <a:shade val="90000"/>
                                            <a:lumMod val="103000"/>
                                        </a:schemeClr>
                                    </a:gs>
                                    <a:gs pos="100000">
                                        <a:schemeClr val="phClr">
                                            <a:shade val="63000"/>
                                            <a:satMod val="120000"/>
                                        </a:schemeClr>
                                    </a:gs>
                                </a:gsLst>
                                <a:lin ang="5400000" scaled="0"/>
                            </a:gradFill>
                        </a:bgFillStyleLst>
                    </a:fmtScheme>
                </a:themeElements>
                <a:objectDefaults/>
            </a:theme>
        </pkg:xmlData>
    </pkg:part>
</pkg:package>