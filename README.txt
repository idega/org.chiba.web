                                   /---------------------------------------------------------------------------/
								   /						New Chiba version update tips					   /
                                   /---------------------------------------------------------------------------/
                                   
> Replace catalogs javascript, style, xslt in org.chiba.web/resources.
  Add components.xsl from the old chiba xslt catalog
  Merge differences between old and new Xform stylesheets
  
> For custom XForms session class creation a custom XFormsSessionManger implementation must be created. For example see com.idega.chiba.web.session.impl package

> Make necessary changes in ComponentsGeneratorImpl class

> Path to chiba Maven repositpory is in com.idega.block.addon pom.xml

> Comment uploadDir property in chiba-config.xml