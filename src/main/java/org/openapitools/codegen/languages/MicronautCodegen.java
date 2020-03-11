package org.openapitools.codegen.languages;

import java.io.File;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.TemporalAmount;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.openapitools.codegen.CliOption;
import org.openapitools.codegen.CodegenConstants;
import org.openapitools.codegen.CodegenOperation;
import org.openapitools.codegen.SupportingFile;
import org.openapitools.codegen.utils.ModelUtils;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.servers.Server;

public class MicronautCodegen extends AbstractJavaCodegen {

	public static final String CLIENT_ID = "clientId";
	public static final String VALIDATION = "validation";

	private boolean generateApiTests = true;

	public MicronautCodegen() {

		cliOptions.add(CliOption.newBoolean(VALIDATION, "Whether to generate validation annotations of not.", true));
		cliOptions.add(CliOption.newString(CLIENT_ID, "ClientId to use."));

		// there is no documentation template yet

		apiDocTemplateFiles.remove("api_doc.mustache");
		apiTestTemplateFiles.remove("api_test.mustache");
		modelDocTemplateFiles.remove("model_doc.mustache");

		// parent flags

		dateLibrary = "do not trigger date type selection";
		additionalProperties.put(CodegenConstants.TEMPLATE_DIR, "Micronaut");

		// add custom type mappings

		typeMapping.put("date", LocalDate.class.getName());
		typeMapping.put("DateTime", OffsetDateTime.class.getName());
		typeMapping.put("array", List.class.getName());
		typeMapping.put("map", Map.class.getName());
		typeMapping.put("boolean", Boolean.class.getName());
		typeMapping.put("string", String.class.getName());
		typeMapping.put("int", Integer.class.getName());
		typeMapping.put("Integer", Integer.class.getName());
		typeMapping.put("long", Long.class.getName());
		typeMapping.put("float", Float.class.getName());
		typeMapping.put("number", Double.class.getName());
		typeMapping.put("BigDecimal", Double.class.getName());
		typeMapping.put("UUID", UUID.class.getName());
		typeMapping.put("URI", URI.class.getName());
	}

	@Override
	public String getName() {
		return "micronaut";
	}

	@Override
	public void processOpts() {

		// reuse api package if other packages are not provided

		var apiPackage = additionalProperties.get(CodegenConstants.API_PACKAGE);
		if (additionalProperties.get(CodegenConstants.MODEL_PACKAGE) == null) {
			additionalProperties.put(CodegenConstants.MODEL_PACKAGE, apiPackage);
		}
		if (additionalProperties.get(CodegenConstants.INVOKER_PACKAGE) == null) {
			additionalProperties.put(CodegenConstants.INVOKER_PACKAGE, apiPackage);
		}
		if (additionalProperties.get("testPackage") == null) {
			additionalProperties.put("testPackage", apiPackage);
		}

		super.processOpts();

		// we do not generate projects, only api, set source and test folder

		projectFolder = "generated-sources";
		projectTestFolder = "generated-test-sources";
		sourceFolder = projectFolder + File.separator + "openapi";
		testFolder = projectTestFolder + File.separator + "openapi";
		if (testPackage == null || testPackage.isEmpty()) {
			testPackage = this.apiPackage;
		}

		// add files to generate

		if (additionalProperties.containsKey(CLIENT_ID)) {
			apiTemplateFiles.put("api_client.mustache", "Client.java");
		}
		if (additionalProperties.get(CodegenConstants.GENERATE_API_TESTS) != null) {
			generateApiTests = (Boolean) additionalProperties.get(CodegenConstants.GENERATE_API_TESTS);
		}
		if (generateApiTests) {
			apiTestTemplateFiles.put("test.mustache", "Spec.java");
			apiTestTemplateFiles.put("test_client.mustache", "Client.java");
			addSupportingFile(testFolder, "HttpResponseAssertions");
		}
	}

	@Override
	public CodegenOperation fromOperation(String path, String httpMethod, Operation source, List<Server> servers) {

		var operation = super.fromOperation(path, httpMethod, source, servers);
		var extensions = operation.vendorExtensions;

		// remove media type */*

		Optional.ofNullable(operation.produces).ifPresent(p -> p.removeIf(m -> "*/*".equals(m.get("mediaType"))));
		Optional.ofNullable(operation.consumes).ifPresent(c -> c.removeIf(m -> "*/*".equals(m.get("mediaType"))));

		// store method and status for micronaut

		extensions.put("httpMethod", httpMethod.toUpperCase().charAt(0) + httpMethod.substring(1).toLowerCase());
		operation.responses.forEach(r -> extensions.put("has" + r.code, true));

		// add wildcard for lists for clients (api client & test client)

		var containerParams = operation.queryParams.stream().filter(p -> p.isContainer).collect(Collectors.toList());
		if (!containerParams.isEmpty()) {
			extensions.put("path", operation.path + "?" + containerParams.stream()
					.map(p -> "{&" + p.baseName + "*}")
					.collect(Collectors.joining()));
		} else {
			extensions.put("path", operation.path);
		}

		// jwt provider for tests

		var hasSecurityJwt = (boolean) operation.vendorExtensions.getOrDefault("has401", false);
		if (generateApiTests && hasSecurityJwt) {
			addSupportingFile(testFolder, "JwtProvider");
			addSupportingFile(testFolder, "JwtBuilder");
		}

		return operation;
	}

	@Override
	public String getSchemaType(Schema s) {
		if (s instanceof StringSchema && "temporal".equals(s.getFormat())) {
			return TemporalAmount.class.getName();
		}
		if (s instanceof StringSchema && "duration".equals(s.getFormat())) {
			return Duration.class.getName();
		}
		if (s instanceof StringSchema && "instant".equals(s.getFormat())) {
			return Instant.class.getName();
		}
		return super.getSchemaType(s);
	}

	@Override
	public String toDefaultValue(Schema schema) {
		if (ModelUtils.isArraySchema(schema)) {
			return "new " + instantiationTypes.get("array") + "<>()";
		}
		if (ModelUtils.isMapSchema(schema)) {
			return "new " + instantiationTypes.get("map") + "<>()";
		}
		return super.toDefaultValue(schema);
	}

	// internal

	void addSupportingFile(String folder, String file) {
		String templateFile = "support/" + file + ".mustache";
		String destinationFilename = folder + "/" + testPackage.toString().replace(".", "/") + "/" + file + ".java";
		SupportingFile supportingFile = new SupportingFile(templateFile, destinationFilename);
		if (!supportingFiles.contains(supportingFile)) {
			supportingFiles.add(supportingFile);
		}
	}
}
